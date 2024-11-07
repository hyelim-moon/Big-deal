document.addEventListener('DOMContentLoaded', function() {
    const memberList = [
        { id: 1, userId: 'user1', type: '일반 회원' },
        { id: 2, userId: 'business1', type: '사업자' }
    ];

    function renderMemberList(members) {
        const memberListElement = document.getElementById('memberList');
        memberListElement.innerHTML = '';
        members.forEach((member, index) => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <th scope="row">${index + 1}</th>
                <td>${member.userId}</td>
                <td>${member.type}</td>
                <td>
                    <button class="btn btn-outline-primary btn-sm" onclick="enableEdit(${member.id})">수정</button>
                    <button class="btn btn-outline-danger btn-sm" onclick="confirmBanMember(${member.id})">탈퇴</button>
                </td>
            `;
            memberListElement.appendChild(row);
        });
    }

    renderMemberList(memberList);

    window.enableEdit = function(id) {
        const member = memberList.find(member => member.id === id);
        document.getElementById('detailMemberID').value = member.userId;
        document.getElementById('detailMemberType').value = member.type;
        const memberDetailModal = new bootstrap.Modal(document.getElementById('memberDetailModal'));
        memberDetailModal.show();
    }

    window.confirmBanMember = function(id) {
        if (confirm(`회원 ID ${id}를 정말로 탈퇴시키겠습니까?`)) {
            banMember(id);
        }
    }

    window.banMember = function(id) {
        const index = memberList.findIndex(member => member.id === id);
        if (index !== -1) {
            memberList.splice(index, 1);
            renderMemberList(memberList);
            alert(`회원 ID ${id}가 탈퇴되었습니다.`);
        }
    }

    window.filterMembers = function() {
        const searchID = document.getElementById('searchID').value.toLowerCase();
        const searchType = document.getElementById('searchType').value.toLowerCase();
        const filteredMembers = memberList.filter(member => {
            const idMatch = member.userId.toLowerCase().includes(searchID);
            const typeMatch = member.type.toLowerCase().includes(searchType);
            return idMatch && typeMatch;
        });
        renderMemberList(filteredMembers);
    }

    window.saveMemberDetails = function() {
        const memberId = document.getElementById('detailMemberID').value;
        const memberType = document.getElementById('detailMemberType').value;
        const member = memberList.find(member => member.userId === memberId);
        if (member) {
            member.type = memberType;
            renderMemberList(memberList);
            alert(`회원 ID ${memberId}의 정보가 수정되었습니다.`);
            const memberDetailModal = bootstrap.Modal.getInstance(document.getElementById('memberDetailModal'));
            memberDetailModal.hide();
        }
    }

    // 메인 페이지로 돌아가기 함수
    window.navigateToMainPage = function() {
        window.location.href = 'managerPage.html';
    }
});
