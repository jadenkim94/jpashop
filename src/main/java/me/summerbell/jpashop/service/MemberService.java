package me.summerbell.jpashop.service;

import lombok.RequiredArgsConstructor;
import me.summerbell.jpashop.domain.Member;
import me.summerbell.jpashop.repository.MemberRepository;
import me.summerbell.jpashop.repository.MemberRepositoryOld;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member);    // 중복 회원 검정
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        //EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberRepository.findById(memberId).get();
    }

    @Transactional
    public void update(Long id,  String name) {
        Member findMember = memberRepository.findById(id).get();
        findMember.setName(name);
    }

}

