package com.academic.erp.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "students",
        indexes = {
                @Index(name = "idx_student_spec_year", columnList = "specialisation_id, join_year")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "roll_number", unique = true, length = 50)
    private String rollNumber;

    @Column(name = "seq_no")
    private Integer seqNo;

    @Column(name = "first_name", nullable = false, length = 120)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 120)
    private String lastName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Path to the photograph file stored on the filesystem.
     * The actual file is stored in the uploads/photos directory, not as a BLOB in the database.
     * Example: "/uploads/photos/uuid-filename.jpg"
     */
    @Column(name = "photograph_path", length = 512)
    private String photographPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id", nullable = false)
    private Domain domain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialisation_id")
    private Specialisation specialisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placement_id")
    private Placement placement;

    @Column(name = "join_year", nullable = false)
    private Integer joinYear;

    @Column(name = "total_credits")
    private Integer totalCredits = 0;

    @Column(name = "cgpa")
    private Double cgpa;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.totalCredits == null) {
            this.totalCredits = 0;
        }
    }
}
