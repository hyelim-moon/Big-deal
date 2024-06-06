document.addEventListener('DOMContentLoaded', function() {
    const reviewList = document.getElementById('reviewList');

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
                <td><button class="edit-button" data-index="${index}" style="background-color: green; color: white;">수정</button></td>
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
        var reviews = JSON.parse(localStorage.getItem('reviews')) || [];
        var review = reviews[index];
        if (review) {
            document.getElementById('reviewModalLabel').innerHTML = '리뷰 수정 - ' + review.franchiseName;
            document.getElementById('reviewText').value = review.reviewText;
            document.querySelector('.rating input[type="range"]').value = review.rating * 2;
            document.querySelector('.rating_star').style.width = `${review.rating * 20}%`;
            document.getElementById('imageUpload').value = ''; // 이미지 초기화
            document.getElementById('submitReviewButton').setAttribute('data-index', index); // 인덱스 저장
            var reviewModal = new bootstrap.Modal(document.getElementById('reviewModal'));
            reviewModal.show();
        }
    }

    // 리뷰 삭제 함수
    function deleteReview(index) {
        var reviews = JSON.parse(localStorage.getItem('reviews')) || [];
        if (index > -1) {
            reviews.splice(index, 1);
        }
        localStorage.setItem('reviews', JSON.stringify(reviews));
        displayReviews();
    }



    document.getElementById('submitReviewButton').addEventListener('click', function(event) {
        event.preventDefault();
        const rating = document.querySelector('.rating input[type="range"]').value / 2;
        const reviewText = document.getElementById('reviewText').value;
        const files = document.getElementById('imageUpload').files;
        const fileName = files.length > 0 ? files[0].name : "No file uploaded";
        const index = this.getAttribute('data-index');

        console.log("등록 정보:", {
            상호명: currentFranchiseName,
            별점: rating,
            리뷰내용: reviewText,
            파일명: fileName
        });

        const reader = new FileReader();
        reader.onloadend = function() {
            const base64Image = reader.result;
            saveReview(currentFranchiseName, rating, reviewText, base64Image, index);
            $('#reviewModal').modal('hide');
            alert("등록이 완료되었습니다.");
        };

        if (files.length > 0) {
            reader.readAsDataURL(files[0]);
        } else {
            saveReview(currentFranchiseName, rating, reviewText, null, index);
            $('#reviewModal').modal('hide');
            alert("등록이 완료되었습니다.");
        }
    });

    function saveReview(franchiseName, rating, reviewText, base64Image, index = null) {
        var reviews = JSON.parse(localStorage.getItem('reviews')) || [];
        var review = {
            franchiseName: franchiseName,
            rating: rating,
            reviewText: reviewText,
            image: base64Image
        };

        if (index !== null) {
            reviews[index] = review; // 수정
            // 서버로 수정 요청 보내기
            fetch('/api/v1/rating', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + localStorage.getItem('accessToken')
                },
                body: JSON.stringify({
                    uuid: review.uuid, // 리뷰의 고유 ID
                    franchiseName: franchiseName,
                    rating: rating,
                    reviewText: reviewText,
                    image: base64Image
                })
            })
            .then(response => response.json())
            .then(data => {
                console.log('Review updated successfully:', data);
                displayReviews();
            })
            .catch(error => {
                console.error('Error updating review:', error);
            });
        } else {
            reviews.push(review); // 추가
            localStorage.setItem('reviews', JSON.stringify(reviews));
            displayReviews();
        }
    }

    // 페이지 로드 시 리뷰 데이터 가져오기 호출
    displayReviews();
});
