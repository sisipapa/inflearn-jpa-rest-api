package com.example.inflearnjparestapi.api;

import com.example.inflearnjparestapi.domain.Member;
import com.example.inflearnjparestapi.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * Entity에 프레젠테이션 영역을 컨트롤 하기 위한 조건들이 들어가게 된다.
     * 수정
     * dev4생성
     * R1
     * R2
     * R3
     * R4
     * @return
     */
    @GetMapping("/api/v1/members")
    public List<Member> memberV1(){
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map( member -> new MemberDto(member.getName()))
                .collect(Collectors.toList());

        return new Result(collect);

    }


    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }

    /**
     * v1 API는 Member Entity를 RequestBody를 바로 받아서 사용하는 예제
     * @param member
     * @return
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * v2 API는 Entity를 파라미터나 API응답결과로 사용하지 않고 별도의 DTO 클래스를 만들어서 사용.
     * @param request
     * @return
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String neme;
    }

    @Data
    static class CreateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class CreateMemberResponse{
        private Long id;
    }
}
