package com.academic.erp.backend.service;

import org.springframework.stereotype.Component;

@Component
public class RollNumberGenerator {

    public String extractPrefix(String program) {
        program = program.toUpperCase();

        if (program.startsWith("M.TECH")) return "MT";
        if (program.startsWith("IMTECH")) return "IMT";
        if (program.startsWith("B.TECH")) return "BT";
        if (program.startsWith("MS")) return "MS";

        throw new IllegalArgumentException("Invalid degree in program: " + program);
    }

    public String extractDepartmentSeries(String program) {
        program = program.toUpperCase();

        if (program.contains("CSE")) return "0";      // 001–150
        if (program.contains("ECE")) return "5";      // 500–599
        if (program.contains("AIDS")) return "7";     // 700–799

        throw new IllegalArgumentException("Invalid department in program: " + program);
    }

    public String formatRollNumber(String prefix, Integer joinYear, Integer sequence, String deptSeries) {
        String seq = String.format("%03d", sequence);
        return prefix + joinYear + deptSeries + seq;
    }
}
