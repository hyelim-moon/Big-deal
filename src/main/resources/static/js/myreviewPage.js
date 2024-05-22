document.addEventListener('DOMContentLoaded', function () {
    loadReviews();
});

function loadReviews() {
    const reviews = JSON.parse(localStorage.getItem('reviews')) || [];
    const reviewList = document.getElementById('reviewList');
    const noReviewsMessage = document.getElementById('noReviewsMessage');

    if (reviews.length === 0) {
        noReviewsMessage.style.display = 'block';
    } else {
        noReviewsMessage.style.display = 'none';

        reviews.forEach((review, index) => {
            const reviewItem = document.createElement('div');
            reviewItem.classList.add('list-group-item');
            reviewItem.setAttribute('onclick', `showReviewDetail(${index})`);

            const reviewTitle = document.createElement('h5');
            reviewTitle.classList.add('mb-1');
            reviewTitle.innerText = `리뷰 ${index + 1}`;

            const reviewDate = document.createElement('small');
            reviewDate.innerText = `작성 날짜: ${new Date().toISOString().split('T')[0]}`;

            reviewItem.appendChild(reviewTitle);
            reviewItem.appendChild(reviewDate);
            reviewList.appendChild(reviewItem);
        });
    }
}

function showReviewDetail(reviewId) {
    const reviews = JSON.parse(localStorage.getItem('reviews')) || [];
    const review = reviews[reviewId];
    const reviewDetailContent = document.getElementById('reviewDetailContent');

    // 리뷰 상세 내용 표시
    reviewDetailContent.innerHTML = `
        <h5>별점: ${'★'.repeat(review.rating)}</h5>
        <p>${review.reviewText}</p>
        ${review.image ? `<img src="${review.image}" alt="첨부된 이미지" class="img-fluid">` : ''}
    `;

    const reviewDetailModal = new bootstrap.Modal(document.getElementById('reviewDetailModal'));
    reviewDetailModal.show();
}
