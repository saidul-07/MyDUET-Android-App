import fitz
import winocr
from PIL import Image, ImageOps, ImageEnhance
import io
import os
import json
import sys

def extract_candidates_from_page(img, dept, page_is_waiting):
    res = winocr.recognize_pil_sync(img)
    
    # Flatten all words with coordinates
    words = []
    for line in res["lines"]:
        for w in line["words"]:
            rect = w["bounding_rect"]
            cx = rect["x"] + rect["width"]/2
            cy = rect["y"] + rect["height"]/2
            words.append({
                "text": w["text"],
                "cx": cx,
                "cy": cy
            })
            
    # Group words into rows (tolerance 22 at 3.0x scale)
    rows = []
    for w in sorted(words, key=lambda x: x["cy"]):
        placed = False
        for r in rows:
            avg_y = sum(x["cy"] for x in r) / len(r)
            if abs(w["cy"] - avg_y) < 22:
                r.append(w)
                placed = True
                break
        if not placed:
            rows.append([w])
            
    candidates = {}
    for r in rows:
        r_sorted = sorted(r, key=lambda x: x["cx"])
        
        if page_is_waiting:
            # Waiting List (3.0x boundaries):
            # Column 1 (SL/Merit): cx < 225
            # Column 2 (Roll): 225 <= cx < 420
            # Column 3 (Name): 420 <= cx < 870
            # Column 4 (Father's Name): 870 <= cx < 1320
            
            merit_parts = [w["text"] for w in r_sorted if w["cx"] < 225]
            roll_parts = [w["text"] for w in r_sorted if 225 <= w["cx"] < 420]
            name_parts = [w["text"] for w in r_sorted if 420 <= w["cx"] < 870]
            father_parts = [w["text"] for w in r_sorted if 870 <= w["cx"] < 1320]
            
            merit_str = "".join(merit_parts)
            merit_clean = "".join(c for c in merit_str if c.isdigit())
            waiting_merit = int(merit_clean) if merit_clean else None
        else:
            # Selected List (3.0x boundaries):
            # Column 1 (SL): cx < 150
            # Column 2 (Roll): 150 <= cx < 360
            # Column 3 (Name): 360 <= cx < 780
            # Column 4 (Father's Name): 780 <= cx < 1230
            
            roll_parts = [w["text"] for w in r_sorted if 150 <= w["cx"] < 360]
            name_parts = [w["text"] for w in r_sorted if 360 <= w["cx"] < 780]
            father_parts = [w["text"] for w in r_sorted if 780 <= w["cx"] < 1230]
            waiting_merit = None
            
        roll_str = "".join(roll_parts)
        roll_clean = "".join(c for c in roll_str if c.isdigit())
        
        if len(roll_clean) == 5:
            roll = int(roll_clean)
            name_str = " ".join(name_parts).strip().upper()
            father_str = " ".join(father_parts).strip().upper()
            
            # Simple cleaning
            name_str = name_str.replace("'", "").replace("\"", "")
            father_str = father_str.replace("'", "").replace("\"", "")
            
            # stand-alone word checks to avoid substring matches like 'SL' in 'ISLAM'
            name_words = name_str.split()
            
            if name_str and len(name_str) > 2 and not any(k in name_words for k in ["APPLICANT", "FATHER", "NAME", "SL", "ROLL", "MERIT"]):
                candidate = {
                    "roll": roll,
                    "name": name_str,
                    "fatherName": father_str,
                    "department": dept,
                    "status": "Waiting" if page_is_waiting else "Selected"
                }
                if page_is_waiting and waiting_merit is not None:
                    candidate["waitingMerit"] = waiting_merit
                candidates[roll] = candidate
    return candidates

def main():
    if len(sys.argv) < 2:
        year = input("Enter the result publishing year (e.g. 2027): ").strip()
    else:
        year = sys.argv[1].strip()
        
    results_dir = os.path.dirname(os.path.abspath(__file__))
    year_dir = os.path.join(results_dir, year)
    
    if not os.path.exists(year_dir):
        print(f"Error: Directory {year_dir} does not exist. Please create it and download the result PDFs first.")
        return
        
    pdf_files = [f for f in os.listdir(year_dir) if f.endswith(".pdf")]
    if not pdf_files:
        print(f"Error: No PDF result sheets found in {year_dir}")
        return
        
    # Department mapping
    # Maps filename prefix/substring to standard dept codes
    dept_map = {
        "ce": "CE", "civil": "CE",
        "cse": "CSE", "computer": "CSE",
        "eee": "EEE", "electrical": "EEE",
        "me": "ME", "mechanical": "ME",
        "che": "ChE", "chemical": "ChE",
        "fe": "FE", "food": "FE",
        "ipe": "IPE", "industrial": "IPE",
        "mme": "MME", "metallurgical": "MME",
        "te": "TE", "textile": "TE",
        "arch": "ARCH", "architecture": "ARCH"
    }
    
    all_candidates = []
    
    for filename in sorted(pdf_files):
        pdf_path = os.path.join(year_dir, filename)
        
        # Determine department from filename
        dept = "Unknown"
        filename_lower = filename.lower()
        for key, val in dept_map.items():
            if key in filename_lower:
                dept = val
                break
                
        print(f"\nProcessing {filename} (Dept: {dept})...")
        doc = fitz.open(pdf_path)
        is_waiting = "waiting" in filename_lower
        
        for page_num in range(len(doc)):
            pix = doc[page_num].get_pixmap(matrix=fitz.Matrix(3.0, 3.0))  # 3x scale
            img = Image.open(io.BytesIO(pix.tobytes("png")))
            
            # Check transition to waiting list on same-file lists
            res_temp = winocr.recognize_pil_sync(img)
            text_temp = res_temp["text"].lower()
            page_is_waiting = is_waiting or "waiting list" in text_temp or "waiting list according to merit" in text_temp
            
            # Run OCR on original image
            cands_orig = extract_candidates_from_page(img, dept, page_is_waiting)
            
            # Preprocess: Grayscale + 3x Contrast Enhancement
            gray_img = ImageOps.grayscale(img)
            enhancer = ImageEnhance.Contrast(gray_img)
            enhanced_img = enhancer.enhance(3.0)
            
            # Run OCR on contrast-enhanced image
            cands_enh = extract_candidates_from_page(enhanced_img, dept, page_is_waiting)
            
            # Merge results for this page
            merged = {}
            for roll, c in cands_orig.items():
                merged[roll] = c
            for roll, c in cands_enh.items():
                if roll not in merged:
                    merged[roll] = c
                else:
                    orig_c = merged[roll]
                    if len(c["name"]) > len(orig_c["name"]):
                        merged[roll]["name"] = c["name"]
                    if len(c["fatherName"]) > len(orig_c["fatherName"]):
                        merged[roll]["fatherName"] = c["fatherName"]
                    if "waitingMerit" in c and "waitingMerit" not in orig_c:
                        merged[roll]["waitingMerit"] = c["waitingMerit"]
                        
            print(f"  Page {page_num}: Extracted {len(merged)} candidates (Original: {len(cands_orig)}, Enhanced: {len(cands_enh)})")
            all_candidates.extend(merged.values())
            
    # Remove duplicate rolls globally
    unique_candidates = {}
    for c in all_candidates:
        roll = c["roll"]
        if roll in unique_candidates:
            if c["status"] == "Selected":
                unique_candidates[roll] = c
        else:
            unique_candidates[roll] = c
            
    final_list = list(unique_candidates.values())
    print(f"\nTotal Candidates Extracted: {len(all_candidates)}")
    print(f"Unique Candidates: {len(final_list)}")
    
    # Assign sequential waitingMerit values for waitlisted candidates in each department
    waiting_counters = {}
    for c in final_list:
        if c["status"] == "Waiting":
            dept = c["department"]
            curr_val = waiting_counters.get(dept, 1)
            c["waitingMerit"] = curr_val
            waiting_counters[dept] = curr_val + 1
            
    print("Waitlist totals by department:")
    for dept, count in sorted(waiting_counters.items()):
        print(f"  {dept:4s}: {count - 1} candidates")
        
    # Save to year/admission_result_<year>.json
    year_json = os.path.join(year_dir, f"admission_result_{year}.json")
    with open(year_json, "w", encoding="utf-8") as f:
        json.dump(final_list, f, indent=2)
    print(f"Generated {year_json}")
    
    # Copy as results/latest_admission_result.json
    latest_json = os.path.join(results_dir, "latest_admission_result.json")
    with open(latest_json, "w", encoding="utf-8") as f:
        json.dump(final_list, f, indent=2)
    print(f"Updated online reference {latest_json}")
    print("\nProcessing complete! Push your changes to GitHub to update the apps.")

if __name__ == "__main__":
    main()
