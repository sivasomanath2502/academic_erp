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

        // 2) Validate duplicate email
        studentRepository.findByEmail(request.getEmail())
                .ifPresent(s -> {
                    throw new RuntimeException("Email already exists");
                });

        // 3) Extract prefix + dept series
        String prefix = rollNumberGenerator.extractPrefix(domain.getProgram());
        String deptSeries = rollNumberGenerator.extractDepartmentSeries(domain.getProgram());

        // 4) Fetch last sequence for this domain + joinYear
        Integer lastSeq = studentRepository
                .findTopByDomain_ProgramAndJoinYearOrderBySeqNoDesc(domain.getProgram(), request.getJoinYear())
                .map(Student::getSeqNo)
                .orElse(0);

        int newSeq = lastSeq + 1;

        // 5) Generate roll number
        String rollNumber = rollNumberGenerator.formatRollNumber(
                prefix,
                request.getJoinYear(),
                newSeq,
                deptSeries
        );

        // 6) Build student entity
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

        // 7) Save
        studentRepository.save(student);

        // 8) Return response
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
