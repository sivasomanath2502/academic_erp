package com.academic.erp.backend.service;

import com.academic.erp.backend.dto.StudentAdmissionRequestDto;
import com.academic.erp.backend.dto.StudentResponseDto;
import com.academic.erp.backend.entity.Domain;
import com.academic.erp.backend.entity.Student;
import com.academic.erp.backend.repository.DomainRepository;
import com.academic.erp.backend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdmissionServiceImpl implements AdmissionService {

    private final DomainRepository domainRepository;
    private final StudentRepository studentRepository;
    private final RollNumberGenerator rollNumberGenerator;

    @Override
    @Transactional
    public StudentResponseDto admitStudent(StudentAdmissionRequestDto request) {

        // 1) Validate domain
        Domain domain = domainRepository.findById(request.getDomainId())
                .orElseThrow(() -> new RuntimeException("Invalid domain ID"));

        // 2) Resolve degree prefix & department range
        String prefix = rollNumberGenerator.extractDegreePrefix(domain.getProgram());
        RollNumberGenerator.DepartmentRange range = rollNumberGenerator.resolveDepartmentRange(domain.getProgram());

        // 3) Fetch last sequence inside the department range for this join year
        String rollBase = rollNumberGenerator.buildRollBase(prefix, request.getJoinYear());

        int lastSeq = studentRepository
                .findTopByJoinYearAndSeqNoBetweenAndRollNumberStartingWithOrderBySeqNoDesc(
                        request.getJoinYear(),
                        range.startInclusive(),
                        range.endInclusive(),
                        rollBase
                )
                .map(Student::getSeqNo)
                .orElse(range.startInclusive() - 1);

        int newSeq = lastSeq + 1;
        if (newSeq > range.endInclusive()) {
            throw new RuntimeException("Seat range exhausted for department: " + domain.getProgram());
        }

        // 4) Generate roll number
        String rollNumber = rollNumberGenerator.formatRollNumber(
                prefix,
                request.getJoinYear(),
                newSeq
        );

        // 5) Build student entity
        Student student = Student.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .photographPath(request.getPhotographPath())
                .domain(domain)
                .joinYear(request.getJoinYear())
                .seqNo(newSeq)
                .rollNumber(rollNumber)
                .build();

        // 6) Save
        studentRepository.save(student);

        // 7) Return response
        return StudentResponseDto.builder()
                .studentId(student.getStudentId())
                .rollNumber(student.getRollNumber())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .domainProgram(domain.getProgram())
                .joinYear(student.getJoinYear())
                .build();
    }
}
