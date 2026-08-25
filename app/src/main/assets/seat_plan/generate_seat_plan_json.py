import fitz
import json
import os
import re
import sys

def parse_seat_plan_pdf(pdf_path):
    doc = fitz.open(pdf_path)
    
    # Department shift mapping based on DUET standard:
    # 1st Shift: 09:30 AM to 12:00 PM
    # 2nd Shift: 02:00 PM to 04:30 PM
    shift_map = {
        "CE": "09:30 AM to 12:00 PM",
        "CSE": "09:30 AM to 12:00 PM",
        "EEE": "09:30 AM to 12:00 PM",
        "Arch.": "09:30 AM to 12:00 PM",
        "FE": "09:30 AM to 12:00 PM",
        "TE": "09:30 AM to 12:00 PM",
        "ME/IPE/MME": "02:00 PM to 4:30 PM",
        "ChE": "02:00 PM to 4:30 PM"
    }
    
    departments = ["CE", "ChE", "CSE", "ME/IPE/MME", "EEE", "Arch.", "FE", "TE"]
    
    def get_clean_room_name(room_words):
        if not room_words:
            return ""
        # Group room words into lines (Y tolerance 4 pixels)
        lines = []
        for w in sorted(room_words, key=lambda x: (x[1]+x[3])/2):
            placed = False
            cy = (w[1] + w[3]) / 2
            for l in lines:
                avg_y = sum((y[1]+y[3])/2 for y in l) / len(l)
                if abs(cy - avg_y) < 4:
                    l.append(w)
                    placed = True
                    break
            if not placed:
                lines.append([w])
                
        # Sort lines vertically
        lines_sorted_vertically = sorted(lines, key=lambda l: sum((y[1]+y[3])/2 for y in l)/len(l))
        
        # Sort words on each line horizontally
        line_texts = []
        for l in lines_sorted_vertically:
            l_sorted_horiz = sorted(l, key=lambda x: x[0])
            line_texts.append(" ".join([w[4] for w in l_sorted_horiz]))
            
        return " ".join(line_texts).strip()

    seat_plans = []
    
    # Page 0 is the summary table, start parsing room-level data from Page 1
    for page_num in range(1, len(doc)):
        page = doc[page_num]
        words = page.get_text("words")
        
        # 1. Determine building name for this page
        building = "Unknown Building"
        page_text = page.get_text("text")
        if "Administrative" in page_text or "SASAB" in page_text:
            building = "Shahid Abu Sayed Administrative Building (SASAB)"
        elif "Academic" in page_text or "SSNIAB" in page_text:
            building = "Shahid Syed Nazrul Islam Academic Building (SSNIAB)"
        elif "Textile" in page_text or "TWB" in page_text:
            building = "Textile Workshop Building (TWB)"
            
        # 2. Find exam date
        exam_date = "02-08-2026"
        date_match = re.search(r"Date:\s*(\d{2}-\d{2}-\d{4})", page_text)
        if date_match:
            exam_date = date_match.group(1)
            
        # 3. Detect department column headers
        headers = []
        for w in words:
            text = w[4].strip()
            if text in departments:
                if w[1] > 220: # Headers are always at the top of the table
                    continue
                headers.append({
                    "dept": text,
                    "cx": (w[0] + w[2]) / 2,
                    "cy": (w[1] + w[3]) / 2,
                    "x0": w[0]
                })
        headers = sorted(headers, key=lambda x: x["cx"])
        
        # 4. Group all words into lines by Y coordinate (tolerance 4 pixels) to scan ranges
        lines = []
        for w in sorted(words, key=lambda x: (x[1]+x[3])/2):
            placed = False
            cy = (w[1] + w[3]) / 2
            for l in lines:
                avg_y = sum((y[1]+y[3])/2 for y in l) / len(l)
                if abs(cy - avg_y) < 4:
                    l.append(w)
                    placed = True
                    break
            if not placed:
                lines.append([w])
                
        # Find ranges in each line
        ranges = []
        for l in lines:
            l_sorted = sorted(l, key=lambda x: x[0])
            
            i = 0
            while i < len(l_sorted):
                w = l_sorted[i]
                text = w[4].strip()
                
                if len(text) == 5 and text.isdigit():
                    found_range = False
                    for lookahead in range(1, 4):
                        if i + lookahead < len(l_sorted):
                            next_w = l_sorted[i + lookahead]
                            next_text = next_w[4].strip()
                            
                            # Clean leading non-digits (e.g. \u201320504)
                            next_clean = re.sub(r'^[^\d]+', '', next_text)
                            
                            if len(next_clean) == 5 and next_clean.isdigit():
                                dist = next_w[0] - w[2]
                                if -10 < dist < 45: # Allow small negative overlap due to dash symbol
                                    start_roll = int(text)
                                    end_roll = int(next_clean)
                                    
                                    r_x0 = w[0]
                                    r_y0 = min(w[1], next_w[1])
                                    r_x1 = next_w[2]
                                    r_y1 = max(w[3], next_w[3])
                                    
                                    ranges.append({
                                        "startRoll": start_roll,
                                        "endRoll": end_roll,
                                        "x0": r_x0,
                                        "y0": r_y0,
                                        "x1": r_x1,
                                        "y1": r_y1,
                                        "cx": (r_x0 + r_x1) / 2,
                                        "cy": (r_y0 + r_y1) / 2
                                    })
                                    i += lookahead
                                    found_range = True
                                    break
                    if found_range:
                        i += 1
                        continue
                i += 1
                
        # 5. Group ranges into distinct row Y coordinates
        row_centers = []
        for r in ranges:
            cy = r["cy"]
            placed = False
            for rc in row_centers:
                if abs(cy - rc) < 10:
                    placed = True
                    break
            if not placed:
                row_centers.append(cy)
        row_centers = sorted(row_centers)
        
        # 6. Determine dynamic Room column max X coordinate
        if headers:
            min_header_x0 = min(h["x0"] for h in headers)
            room_col_max_x = min_header_x0 - 10
        else:
            room_col_max_x = 210
            
        # Gather all room words on the left
        all_room_words = []
        for w in words:
            w_text = w[4].strip()
            if any(k in w_text for k in ["Shahid", "Abu", "Sayed", "Administrative", "Building", "SASAB", "Academic", "SSNIAB", "Textile", "Workshop", "TWB", "Room", "No.", "Date:", "Shift", "2026", "Note:"]):
                continue
            if w[2] < room_col_max_x:
                all_room_words.append(w)
                
        # Assign room words to their closest row center
        row_to_words = {rc: [] for rc in row_centers}
        for w in all_room_words:
            w_cy = (w[1] + w[3]) / 2
            if not row_centers:
                continue
            closest_rc = min(row_centers, key=lambda rc: abs(w_cy - rc))
            if abs(w_cy - closest_rc) < 35:
                row_to_words[closest_rc].append(w)
                
        # Reconstruct room name for each row center
        row_to_room_name = {}
        for rc, r_words in row_to_words.items():
            room_name = get_clean_room_name(r_words)
            room_name = re.sub(r'\s+', ' ', room_name)
            if room_name.isdigit() and len(room_name) <= 2:
                room_name = ""
            row_to_room_name[rc] = room_name

        # 7. Match each range with its room and department
        for r in ranges:
            if not row_centers:
                continue
            closest_rc = min(row_centers, key=lambda rc: abs(r["cy"] - rc))
            room_name = row_to_room_name.get(closest_rc, "")
            
            closest_header = None
            min_dist = float("inf")
            for h in headers:
                dist = abs(r["cx"] - h["cx"])
                if dist < min_dist:
                    min_dist = dist
                    closest_header = h
                    
            dept = closest_header["dept"] if closest_header else "Unknown"
            shift = shift_map.get(dept, "09:30 AM to 12:00 PM")
            
            seat_plans.append({
                "startRoll": r["startRoll"],
                "endRoll": r["endRoll"],
                "building": building,
                "room": room_name,
                "department": dept,
                "examDate": exam_date,
                "shift": shift
            })
            
    return seat_plans

def main():
    if len(sys.argv) < 2:
        year = input("Enter the seat plan year (e.g. 2026): ").strip()
    else:
        year = sys.argv[1].strip()
        
    base_dir = os.path.dirname(os.path.abspath(__file__))
    year_dir = os.path.join(base_dir, year)
    
    if not os.path.exists(year_dir):
        print(f"Error: Directory {year_dir} does not exist.")
        return
        
    pdf_files = [f for f in os.listdir(year_dir) if f.endswith(".pdf")]
    if not pdf_files:
        print(f"Error: No PDF files found in {year_dir}")
        return
        
    pdf_path = os.path.join(year_dir, pdf_files[0])
    print(f"Parsing seat plan PDF: {pdf_path}")
    
    plans = parse_seat_plan_pdf(pdf_path)
    print(f"Extracted {len(plans)} seat plan entries.")
    
    out_json = os.path.join(year_dir, f"seat_plan_{year}.json")
    with open(out_json, "w", encoding="utf-8") as f:
        json.dump(plans, f, indent=2)
    print(f"Saved database to {out_json}")
    
    latest_json = os.path.join(base_dir, "latest_seat_plan.json")
    with open(latest_json, "w", encoding="utf-8") as f:
        json.dump(plans, f, indent=2)
    print(f"Updated online reference {latest_json}")
    print("\nProcessing complete! Push your changes to GitHub to update the apps.")

if __name__ == "__main__":
    main()
