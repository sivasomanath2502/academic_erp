package com.academic.erp.backend.service;

import org.springframework.stereotype.Component;

@Component
public class RollNumberGenerator {

    public String extractDegreePrefix(String program) {
        String normalized = program.toUpperCase();

        if (normalized.contains("IM.TECH") || normalized.startsWith("IMTECH")) {
            return "IM";
        }

        if (normalized.contains("M.TECH")) {
            return "MT";
        }

        if (normalized.contains("B.TECH")) {
            return "BT";
        }

        if (normalized.startsWith("MS")) {
            return "MS";
        }

        throw new IllegalArgumentException("Invalid degree in program: " + program);
    }

    public DepartmentRange resolveDepartmentRange(String program) {
        String normalized = program.toUpperCase();

        if (normalized.contains("CSE")) {
            return new DepartmentRange(1, 200);
        }
        if (normalized.contains("ECE")) {
            return new DepartmentRange(501, 600);
        }
        if (normalized.contains("AIDS")) {
            return new DepartmentRange(701, 800);
        }

        throw new IllegalArgumentException("Unknown department in program: " + program);
    }

    public String buildRollBase(String prefix, Integer joinYear) {
        return prefix + String.format("%04d", joinYear);
    }

    public String formatRollNumber(String prefix, Integer joinYear, Integer sequence) {
        String rollBase = buildRollBase(prefix, joinYear);
        String seq = String.format("%03d", sequence);
        return rollBase + seq;
    }

    public record DepartmentRange(int startInclusive, int endInclusive) {}
}
