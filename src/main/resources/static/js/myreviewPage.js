document.addEventListener('DOMContentLoaded', function() {
    const reviewList = document.getElementById('reviewList');

    // 내 정보 페이지로 이동
    window.navigateToMyPage = function() {
        window.location.href = 'myPage.html';
    };

    // 로컬 스토리지에서 리뷰 데이터를 읽어와서 표시하는 함수
    function displayReviews() {
        if (!reviewList) {
            console.error('Review list element not found');
            return;
        }

        const reviews = JSON.parse(localStorage.getItem('reviews')) || [];
        reviewList.innerHTML = ''; // 기존 리뷰 목록 초기화

        reviews.forEach((review, index) => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${review.franchiseName || ''}</td>
                <td>${review.rating}</td>
                <td>${review.reviewText}</td>
                <td><img src="${review.image}" alt="Review Image" style="width: 100px;"></td>
                <td><button class="edit-button btn btn-success btn-sm" data-index="${index}">수정</button></td>
                <td><button class="delete-button btn btn-danger btn-sm" data-index="${index}">삭제</button></td>
            `;
            reviewList.appendChild(row);
        });

        // 모든 삭제 버튼에 이벤트 리스너 추가
        document.querySelectorAll('.delete-button').forEach(button => {
            button.addEventListener('click', function() {
                const index = this.getAttribute('data-index');
                deleteReview(index);
            });
        });

        // 모든 수정 버튼에 이벤트 리스너 추가
        document.querySelectorAll('.edit-button').forEach(button => {
            button.addEventListener('click', function() {
                const index = this.getAttribute('data-index');
                editReview(index);
            });
        });
    }

    // 리뷰 수정 함수
    function editReview(index) {
        const reviews = JSON.parse(localStorage.getItem('reviews')) || [];
        const review = reviews[index];
        if (review) {
            document.getElementById('reviewModalLabel').innerHTML = '리뷰 수정';
            document.getElementById('reviewText').value = review.reviewText;
            document.querySelector('.modal-body input[type="range"]').value = review.rating * 2;
            document.getElementById('imageUpload').value = ''; // 이미지 초기화
            document.getElementById('submitReviewButton').setAttribute('data-index', index); // 인덱스 저장
            const reviewModal = new bootstrap.Modal(document.getElementById('reviewModal'));
            reviewModal.show();
        }
    }

    // 리뷰 삭제 함수
    function deleteReview(index) {
        const reviews = JSON.parse(localStorage.getItem('reviews')) || [];
        if (index > -1) {
            reviews.splice(index, 1);
        }
        localStorage.setItem('reviews', JSON.stringify(reviews));
        displayReviews();
    }

    // 페이지 로드 시 리뷰 데이터 가져오기 호출
    displayReviews();
});
