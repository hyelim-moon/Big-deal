document.addEventListener('DOMContentLoaded', function() {
    const reviewList = document.getElementById('reviewList');
    const accessToken = localStorage.getItem('accessToken'); // AccessToken을 로컬 스토리지에서 가져옵니다.

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
                <td><button class="delete-button" data-index="${index}" style="background-color: red; color: white;">X</button></td>
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
    }

    // 리뷰 삭제 함수
    function deleteReview(index) {
        var reviews = JSON.parse(localStorage.getItem('reviews')) || [];
        const review = reviews[index];
        if (!review) return;

        const requestPayload = {
            franchiseName: review.franchiseName,
            rating: review.rating,
            reviewText: review.reviewText,
            image: review.image
        };

        fetch('http://localhost:8080/api/v1/rating', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + accessToken
            },
            body: JSON.stringify(requestPayload)
        })
        .then(response => response.json())
        .then(data => {
            console.log('Review deleted successfully:', data);
            // 서버에서 성공적으로 삭제되면 로컬 스토리지에서도 삭제
            if (index > -1) {
                reviews.splice(index, 1);
            }
            localStorage.setItem('reviews', JSON.stringify(reviews));
            displayReviews();
        })
        .catch(error => {
            console.error('Error deleting review:', error);
        });
    }

    // 페이지 로드 시 리뷰 데이터 가져오기 호출
    displayReviews();
});
