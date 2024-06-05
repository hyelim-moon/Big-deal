var mapContainer = document.getElementById('map'),
    mapOption = {
        center: new kakao.maps.LatLng(37.5518911, 126.9917937), // 지도의 중심 좌표
        level: 11 // 지도의 확대 레벨
    };

var map = new kakao.maps.Map(mapContainer, mapOption); // 지도 생성
var markers = []; // 마커를 저장할 배열
var infowindow = new kakao.maps.InfoWindow({removable: true}); // 전역 인포윈도우 객체

// 마커 클러스터러 생성
var clusterer = new kakao.maps.MarkerClusterer({
    map: map,
    averageCenter: true,
    minLevel: 10
});

// 장소 검색 객체를 생성합니다
var ps = new kakao.maps.services.Places();

var currentFranchiseName = '';  // 전역 변수로 가맹점 이름 저장

var franchises = []; // 가맹점 데이터를 저장할 배열

function showReviewModal(franchiseName) {
    const token = localStorage.getItem('accessToken'); // 로컬 스토리지에서 토큰 가져오기

    // 토큰이 없으면 로그인이 필요하다는 경고창을 띄우고 로그인 페이지로 이동
    if (!token) {
        alert('로그인이 필요합니다.');
        window.location.href = 'loginPage.html';
    } else {
        currentFranchiseName = franchiseName;  // 전역 변수 업데이트
        document.getElementById('reviewModalLabel').innerHTML = '평점 등록 - ' + franchiseName;
        document.getElementById('reviewText').placeholder = franchiseName + '에 대한 리뷰를 입력해주세요...';
        var reviewModal = new bootstrap.Modal(document.getElementById('reviewModal'));
        reviewModal.show();
    }
}

// 기존의 loadData 함수 내에서 마커 이벤트 리스너 수정
function loadData() {
    var la = 37.5518911;
    var lo = 126.9917937;

    var url = `/api/v1/franchise?la=${la}&lo=${lo}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            franchises = data; // 가맹점 데이터 저장

            var localMarkers = data.map(function (franchise) {
                var markerPosition = new kakao.maps.LatLng(franchise.latitude, franchise.longitude);
                var marker = new kakao.maps.Marker({
                    position: markerPosition
                });

                kakao.maps.event.addListener(marker, 'click', function() {
                    var content = '<div style="padding:20px; border: 5px solid #9d9d9d; white-space: nowrap;">' +
                                  '<strong>상호명:</strong> ' + franchise.name + '<br>' +
                                  '<strong>주소:</strong> ' + franchise.mapAddress + '<br>' +
                                  '<strong>카드:</strong> ' + franchise.card + '<br>' +
                                  '<strong>지류:</strong> ' + franchise.paper + '<br>' +
                                  '<strong>모바일:</strong> ' + franchise.mobile + '<br>' +
                                  '<button style="width: 100%; height: 40px;" onclick="showReviewModal(\'' + franchise.name + '\')">리뷰 쓰기</button>' +
                                  '</div>';
                    infowindow.setContent(content);
                    infowindow.open(map, marker);
                });

                return marker;
            });

            clusterer.addMarkers(localMarkers); // 클러스터러에 마커들을 추가
            markers = markers.concat(localMarkers);
        })
        .catch(err => {
            console.error('Error loading the franchise data:', err);
            alert('데이터를 불러오는 중 오류가 발생했습니다.');
        });

    // 지도 클릭 시 인포윈도우 닫기
    kakao.maps.event.addListener(map, 'click', function() {
        infowindow.close();
    });
}

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

    ratingInput.addEventListener('input', function() {
        ratingStar.style.width = `${this.value * 10}%`;
    });

    submitButton.addEventListener('click', function(event) {
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

        // 모달 닫기
        $('#reviewModal').modal('hide');
        alert("등록이 완료되었습니다.");
    });
});

function submitReview() {
    var rating = $('input[type="range"]').val();
    var reviewText = $('#reviewText').val();
    var imageUpload = $('#imageUpload')[0].files[0];

    if (imageUpload) {
        var reader = new FileReader();
        reader.onloadend = function() {
            var base64Image = reader.result;
            saveReview(rating, reviewText, base64Image);
        };
        reader.readAsDataURL(imageUpload);
    } else {
        saveReview(rating, reviewText, null);
    }

    $('#reviewModal').modal('hide');
}

function saveReview(rating, reviewText, base64Image) {
    var review = {
        rating: rating / 2,
        reviewText: reviewText,
        image: base64Image
    };

    var reviews = JSON.parse(localStorage.getItem('reviews')) || [];
    reviews.push(review);
    localStorage.setItem('reviews', JSON.stringify(reviews));

    console.log("리뷰 저장:", review);
}

function searchPlaces() {
    var keyword = document.getElementById('keyword').value;
    if (!keyword.replace(/^\s+|\s+$/g, '')) {
        alert('키워드를 입력해주세요!');
        return false;
    }

    ps.keywordSearch(keyword, placesSearchCB);
}

function placesSearchCB(data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) {
        displayPlaces(data);
        displayPagination(pagination);
    } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
        alert('검색 결과가 존재하지 않습니다.');
        removeAllChildNods(document.getElementById('placesList'));
    } else if (status === kakao.maps.services.Status.ERROR) {
        alert('검색 결과 중 오류가 발생했습니다.');
    }
}

function displayPlaces(places) {
    var listEl = document.getElementById('placesList'),
        menuEl = document.getElementById('menu_wrap'),
        fragment = document.createDocumentFragment(),
        bounds = new kakao.maps.LatLngBounds();

    removeAllChildNods(listEl);
    removeMarker();

    // 검색된 장소 중 가맹점만 필터링
    var filteredPlaces = places.filter(function(place) {
        return franchises.some(function(franchise) {
            return franchise.name === place.place_name;
        });
    });

    filteredPlaces.forEach(function(place, index) {
        var markerPosition = new kakao.maps.LatLng(place.y, place.x);
        var marker = new kakao.maps.Marker({
            position: markerPosition
        });
        markers.push(marker);

        var itemEl = getListItem(index, place); // 아이템 생성
        fragment.appendChild(itemEl);
        bounds.extend(markerPosition); // 마커의 위치를 bounds에 추가

        kakao.maps.event.addListener(marker, 'mouseover', function() {
            infowindow.setContent('<div style="padding:5px; white-space: nowrap;">' + place.place_name + '</div>');
            infowindow.open(map, marker);
        });

        kakao.maps.event.addListener(marker, 'mouseout', function() {
            infowindow.close();
        });

        itemEl.onmouseover = function() {
            infowindow.setContent('<div style="padding:5px; white-space: nowrap;">' + place.place_name + '</div>');
            infowindow.open(map, marker);
        };

        itemEl.onmouseout = function() {
            infowindow.close();
        };
    });

    // 필터링된 가맹점이 없을 경우 메시지 표시
    if (filteredPlaces.length === 0) {
        var itemEl = document.createElement('li');
        itemEl.innerHTML = '검색 결과가 없습니다.';
        fragment.appendChild(itemEl);
    }

    listEl.appendChild(fragment);
    menuEl.scrollTop = 0;

    // 검색된 장소의 중심으로 지도 이동 및 확대 레벨 설정
    if (places.length > 0) {
        var placeCenter = new kakao.maps.LatLng(places[0].y, places[0].x);
        map.setCenter(placeCenter);
        map.setLevel(3);  // 숫자가 작을수록 더 확대됨
    }
}

function getListItem(index, place) {
    var el = document.createElement('li'),
        itemStr = '<span class="markerbg marker_' + (index + 1) + '"></span>' +
                  '<div class="info">' +
                  '<h5>' + place.place_name + '</h5>' +
                  '<span>' + '주소 : ' + place.address_name + '<br>' +
                            '도로명 주소 : ' + place.road_address_name + '<br>' +
                            '카테고리 : ' + place.category_name + '<br>' +
                            '장소 주소 : ' + place.place_url + '</span>' +
                  '</div>';

    el.innerHTML = itemStr;
    el.className = 'item';

    return el;
}

function removeMarker() {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }
    markers = [];
}

function displayPagination(pagination) {
    var paginationEl = document.getElementById('pagination'),
        fragment = document.createDocumentFragment(),
        i;

    while (paginationEl.hasChildNodes()) {
        paginationEl.removeChild(paginationEl.lastChild);
    }

    for (i = 1; i <= pagination.last; i++) {
        var el = document.createElement('a');
        el.href = "#";
        el.innerHTML = i;

        if (i === pagination.current) {
            el.className = 'on';
        } else {
            el.onclick = (function(i) {
                return function() {
                    pagination.gotoPage(i);
                }
            })(i);
        }

        fragment.appendChild(el);
    }
    paginationEl.appendChild(fragment);
}

function removeAllChildNods(el) {
    while (el.hasChildNodes()) {
        el.removeChild(el.lastChild);
    }
}

window.onload = loadData; // 페이지 로드 시 데이터 로드 함수 호출

document.addEventListener('DOMContentLoaded', function() {
    const myPageButton = document.getElementById('mypage-button');

    if (myPageButton) {
        myPageButton.addEventListener('click', function() {
            const token = localStorage.getItem('accessToken');
            if (!token) {
                alert('로그인이 필요합니다.');
                window.location.href = 'loginPage.html';
            } else {
                window.location.href = 'myPage.html';
            }
        });
    }
});

document.addEventListener('DOMContentLoaded', function() {
    const currentLocationButton = document.getElementById('current-location-button');

    if (currentLocationButton) {
        currentLocationButton.addEventListener('click', function() {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function(position) {
                    const lat = position.coords.latitude;
                    const lon = position.coords.longitude;
                    const locPosition = new kakao.maps.LatLng(lat, lon);

                    map.setCenter(locPosition);
                    map.setLevel(3);

                    const marker = new kakao.maps.Marker({
                        position: locPosition,
                        map: map
                    });

                    const infowindowContent = document.createElement('div');
                    infowindowContent.innerHTML = '현재 위치';
                    infowindowContent.style.padding = '5px';  // 패딩 설정
                    infowindowContent.style.whiteSpace = 'nowrap';  // 줄바꿈 방지
                    infowindow.setContent(infowindowContent);

                    kakao.maps.event.addListener(marker, 'click', function() {
                        infowindow.open(map, marker);
                    });

                    kakao.maps.event.addListener(map, 'click', function(mouseEvent) {
                        infowindow.close();
                    });

                }, function(err) {
                    alert('현재 위치를 찾을 수 없습니다.');
                });
            } else {
                alert('Geolocation을 사용할 수 없습니다.');
            }
        });
    }
});
