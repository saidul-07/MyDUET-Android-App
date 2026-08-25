import json
import os
import sys

def main():
    if len(sys.argv) < 2:
        year = input("Enter the seat plan year (e.g. 2026): ").strip()
    else:
        year = sys.argv[1].strip()
        
    seat_plan_dir = os.path.dirname(os.path.abspath(__file__))
    year_dir = os.path.join(seat_plan_dir, year)
    
    candidates_json = os.path.join(year_dir, f"valid_candidates_{year}.json")
    seat_plan_json = os.path.join(year_dir, f"seat_plan_{year}.json")
    
    if not os.path.exists(candidates_json):
        print(f"Error: Candidate JSON {candidates_json} does not exist. Run generate_candidates_json.py first.")
        return
        
    if not os.path.exists(seat_plan_json):
        print(f"Error: Seat Plan JSON {seat_plan_json} does not exist. Run generate_seat_plan_json.py first.")
        return
        
    with open(candidates_json, "r", encoding="utf-8") as f:
        candidates = json.load(f)
        
    with open(seat_plan_json, "r", encoding="utf-8") as f:
        ranges = json.load(f)
        
    print(f"Loaded {len(candidates)} candidates and {len(ranges)} seat plan ranges.")
    
    combined_list = []
    unmatched = 0
    
    for cand in candidates:
        roll = cand["roll"]
        matched = False
        for r in ranges:
            if r["startRoll"] <= roll <= r["endRoll"]:
                combined_entry = {
                    "roll": roll,
                    "name": cand["name"],
                    "fatherName": cand["fatherName"],
                    "building": r["building"],
                    "room": r["room"],
                    "department": r["department"],
                    "examDate": r["examDate"],
                    "shift": r["shift"]
                }
                combined_list.append(combined_entry)
                matched = True
                break
        if not matched:
            unmatched += 1
            
    print(f"Successfully merged {len(combined_list)} candidate seat plans.")
    print(f"Candidates without seat plan matches: {unmatched}")
    
    # Save the combined list back to seat_plan_<year>.json (replacing ranges with individual records)
    with open(seat_plan_json, "w", encoding="utf-8") as f:
        json.dump(combined_list, f, indent=2)
    print(f"Updated unified seat plan database: {seat_plan_json}")
    
    # Copy to latest_seat_plan.json for online reference
    latest_json = os.path.join(seat_plan_dir, "latest_seat_plan.json")
    with open(latest_json, "w", encoding="utf-8") as f:
        json.dump(combined_list, f, indent=2)
    print(f"Updated online reference: {latest_json}")
    
    # Optional cleanup: remove valid_candidates_<year>.json since it is now merged
    try:
        os.remove(candidates_json)
        print(f"Cleaned up temporary candidates JSON: {candidates_json}")
    except Exception as e:
        pass
        
    print("\nProcessing complete! Push your changes to GitHub to update the apps.")

if __name__ == "__main__":
    main()
