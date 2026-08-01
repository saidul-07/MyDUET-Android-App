package com.example.myduet.repositories;

import android.content.Context;
import com.example.myduet.models.SeatPlan;
import com.example.myduet.utils.JsonUtils;
import java.util.List;

public class SeatPlanRepository {
    private List<SeatPlan> cachedSeatPlans;

    public SeatPlanRepository(Context context) {
        // Load JSON once and cache it
        this.cachedSeatPlans = JsonUtils.loadSeatPlanFromAssets(context, "seat_plan/seat_plan_2024.json");
    }

    public SeatPlan searchSeatPlan(int roll) {
        if (cachedSeatPlans == null) return null;
        
        for (SeatPlan plan : cachedSeatPlans) {
            if (plan.isInRange(roll)) {
                return plan;
            }
        }
        return null;
    }
}