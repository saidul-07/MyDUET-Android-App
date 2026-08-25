import fitz
import json
import os
import sys

def parse_candidate_pdfs(candidates_dir):
    pdf_files = [f for f in os.listdir(candidates_dir) if f.endswith(".pdf")]
    
    candidates = []
    
    for filename in sorted(pdf_files):
        pdf_path = os.path.join(candidates_dir, filename)
        doc = fitz.open(pdf_path)
        print(f"Parsing candidate PDF: {filename} ({len(doc)} pages)...")
        
        for page_num in range(len(doc)):
            page = doc[page_num]
            words = page.get_text("words")
            
            # Group words into rows (Y tolerance 5 pixels)
            rows = []
            for w in sorted(words, key=lambda x: (x[1]+x[3])/2):
                placed = False
                cy = (w[1] + w[3]) / 2
                for r in rows:
                    avg_y = sum((y[1]+y[3])/2 for y in r) / len(r)
                    if abs(cy - avg_y) < 5:
                        r.append(w)
                        placed = True
                        break
                if not placed:
                    rows.append([w])
                    
            for r in rows:
                r_sorted = sorted(r, key=lambda x: x[0])
                
                # Check for roll number in Roll column (100 <= cx < 160)
                roll_words = [w[4] for w in r_sorted if 100 <= (w[0]+w[2])/2 < 160]
                roll_str = "".join(roll_words)
                
                if len(roll_str) == 5 and roll_str.isdigit():
                    roll = int(roll_str)
                    
                    # Gather words in candidate info columns (250 <= cx < 660)
                    info_words = [w for w in r_sorted if 250 <= (w[0]+w[2])/2 < 660]
                    if len(info_words) > 0:
                        # Find the largest horizontal gap to separate Name and Father's Name
                        split_idx = -1
                        max_gap = -1
                        for idx in range(len(info_words) - 1):
                            gap = info_words[idx+1][0] - info_words[idx][2] # next_x0 - curr_x1
                            if gap > max_gap:
                                max_gap = gap
                                split_idx = idx
                                
                        if max_gap > 15 and split_idx != -1:
                            name = " ".join([w[4] for w in info_words[:split_idx+1]]).strip().upper()
                            father = " ".join([w[4] for w in info_words[split_idx+1:]]).strip().upper()
                        else:
                            name = " ".join([w[4] for w in info_words]).strip().upper()
                            father = ""
                            
                        # Clean up formatting
                        name = name.replace("'", "").replace("\"", "")
                        father = father.replace("'", "").replace("\"", "")
                        
                        candidates.append({
                            "roll": roll,
                            "name": name,
                            "fatherName": father
                        })
                        
    return candidates

def main():
    if len(sys.argv) < 2:
        year = input("Enter the candidate list year (e.g. 2026): ").strip()
    else:
        year = sys.argv[1].strip()
        
    seat_plan_dir = os.path.dirname(os.path.abspath(__file__))
    candidates_dir = os.path.join(os.path.dirname(os.path.dirname(seat_plan_dir)), "assets", "valid_candidates")
    
    if not os.path.exists(candidates_dir):
        # Fallback to local sibling folder if not inside standard android directory structure
        candidates_dir = os.path.join(os.path.dirname(seat_plan_dir), "valid_candidates")
        
    if not os.path.exists(candidates_dir):
        print(f"Error: Candidate PDFs directory {candidates_dir} does not exist.")
        return
        
    candidates = parse_candidate_pdfs(candidates_dir)
    print(f"\nSuccessfully extracted {len(candidates)} candidates.")
    
    # Save to assets/seat_plan/<year>/valid_candidates_<year>.json
    year_dir = os.path.join(seat_plan_dir, year)
    os.makedirs(year_dir, exist_ok=True)
    
    out_json = os.path.join(year_dir, f"valid_candidates_{year}.json")
    with open(out_json, "w", encoding="utf-8") as f:
        json.dump(candidates, f, indent=2)
    print(f"Saved database to {out_json}")
    
    # Copy as assets/seat_plan/latest_valid_candidates.json
    latest_json = os.path.join(seat_plan_dir, "latest_valid_candidates.json")
    with open(latest_json, "w", encoding="utf-8") as f:
        json.dump(candidates, f, indent=2)
    print(f"Updated online reference {latest_json}")
    print("\nProcessing complete! Push your changes to GitHub to update the apps.")

if __name__ == "__main__":
    main()
