package me.summerbell.jpashop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.summerbell.jpashop.domain.Member;
import me.summerbell.jpashop.service.MemberService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> members = memberService.findMembers();

        List<MemberDto> memberDtoList = members.stream().map(m -> new MemberDto(m.getName())).collect(Collectors.toList());

        return new Result(memberDtoList);
    }

    /**
     * 감싸기..
     */
    @Data
    @AllArgsConstructor
    private static class Result<T>{
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Validated Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Validated CreateMemberRequest request){
        Member newMember = new Member();
        newMember.setName(request.getName());
        return new CreateMemberResponse( memberService.join(newMember));
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable Long id,
                                               @RequestBody @Validated UpdateMemberRequest request){

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());

    }


    @Data
    @AllArgsConstructor
    private static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    private static class UpdateMemberRequest{
        @NotEmpty
        private String name;
    }



    @Data
    private static class CreateMemberRequest{
        private String name;
    }


    @Data
    private static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
