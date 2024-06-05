document.addEventListener('DOMContentLoaded', function() {
    const ratingInput = document.querySelector('.rating input[type="range"]');
    const ratingStar = document.querySelector('.rating_star');
    const reviewTextElement = document.getElementById('reviewText');
    const imageUploadElement = document.getElementById('imageUpload');
    const submitButton = document.getElementById('submitReviewButton');
    const reviewModalElement = document.getElementById('reviewModal');

    if (!reviewModalElement) {
        console.error('Modal element not found');
        return; // 모달 요소가 없으면 초기화 중단
    }

    const reviewModal = new bootstrap.Modal(reviewModalElement);

    // 별점 입력 값 변경 시, 별점 표시 업데이트
    ratingInput.addEventListener('input', function() {
        ratingStar.style.width = `${this.value * 10}%`;
    });

    // 리뷰 제출 버튼 클릭 시
    submitButton.addEventListener('click', function(event) {
        event.preventDefault(); // 폼 제출 기본 동작 방지

        const rating = ratingInput.value / 2;
        const reviewText = reviewTextElement.value;
        const files = imageUploadElement.files;
        const fileName = files.length > 0 ? files[0].name : "No file uploaded";

        console.log("등록 정보:", {
            상호명: currentFranchiseName,
            별점: rating,
            리뷰내용: reviewText,
            파일명: fileName
        });

        // 리뷰 제출 후 모달 닫기
        reviewModal.hide();

        // 리뷰 데이터를 서버로 전송 (여기서는 예시로 콘솔에 출력)
        fetch('/api/v1/reviews', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('accessToken')
            },
            body: JSON.stringify({
                franchiseName: currentFranchiseName,
                rating: rating,
                reviewText: reviewText,
                fileName: fileName
            })
        })
        .then(response => response.json())
        .then(data => {
            console.log('Review submitted successfully:', data);
            // 성공 시 추가 작업 (예: 알림 표시, 리뷰 목록 갱신 등)
        })
        .catch(error => {
            console.error('Error submitting review:', error);
            // 실패 시 추가 작업 (예: 오류 메시지 표시)
        });
    });
});
