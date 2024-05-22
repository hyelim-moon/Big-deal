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

function showReviewModal(franchiseName) {
    currentFranchiseName = franchiseName;  // 전역 변수 업데이트
    document.getElementById('reviewModalLabel').innerHTML = '평점 등록 - ' + franchiseName;
    document.getElementById('reviewText').placeholder = franchiseName + '에 대한 리뷰를 입력해주세요...';
    var reviewModal = new bootstrap.Modal(document.getElementById('reviewModal'));
    reviewModal.show();
}

// 기존의 loadData 함수 내에서 마커 이벤트 리스너 수정
function loadData() {
    var la = 37.5518911;
    var lo = 126.9917937;

    var url = `/api/v1/franchise?la=${la}&lo=${lo}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
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

    // 별점 입력 시 별의 시각적 표현 업데이트
    ratingInput.addEventListener('input', function() {
        ratingStar.style.width = `${this.value * 10}%`;
    });

    // 리뷰 등록 버튼 클릭 이벤트
    submitButton.addEventListener('click', function() {
        // 평점을 0-10 범위에서 0.5-5 범위로 조정
        const rating = ratingInput.value / 2;
        const reviewText = reviewTextElement.value;
        const files = imageUploadElement.files;
        const fileName = files.length > 0 ? files[0].name : "No file uploaded";

        // 콘솔에 정보 출력
        console.log("상호명: " + currentFranchiseName); // 현재 선택된 가맹점 이름
        console.log("별점: " + rating + "점"); // 계산된 별점
        console.log("리뷰 내용: " + reviewText); // 입력된 리뷰 내용
        console.log("업로드한 파일: " + fileName); // 업로드된 파일의 이름

/*
        // AJAX 요청을 통해 서버에 데이터 전송
        if (window.XMLHttpRequest) { // modern browsers
            var xhr = new XMLHttpRequest();
        } else { // for older IE versions
            var xhr = new ActiveXObject("Microsoft.XMLHTTP");
        }

        xhr.open("POST", "/api/reviews", true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.onreadystatechange = function() {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                // 요청 처리 성공 시
                console.log("Response:", this.responseText);
            }
        };
        xhr.send(JSON.stringify({
            franchiseName: currentFranchiseName,
            rating: rating,
            reviewText: reviewText,
            fileName: fileName
        }));
*/
    });
});



// 키워드 검색을 요청하는 함수
function searchPlaces() {
    var keyword = document.getElementById('keyword').value;
    if (!keyword.replace(/^\s+|\s+$/g, '')) {
        alert('키워드를 입력해주세요!');
        return false;
    }

    // 장소검색 객체를 통해 키워드로 장소검색을 요청
    ps.keywordSearch(keyword, placesSearchCB);
}

// 장소검색이 완료됐을 때 호출되는 콜백함수
function placesSearchCB(data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) {
        displayPlaces(data);
        displayPagination(pagination);
    } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
        alert('검색 결과가 존재하지 않습니다.');
    } else if (status === kakao.maps.services.Status.ERROR) {
        alert('검색 결과 중 오류가 발생했습니다.');
    }
}

// 검색 결과 목록과 마커를 표출하는 함수
function displayPlaces(places) {
    var listEl = document.getElementById('placesList'),
        menuEl = document.getElementById('menu_wrap'),
        fragment = document.createDocumentFragment(),
        bounds = new kakao.maps.LatLngBounds();

    removeAllChildNods(listEl);
    removeMarker();

    console.log("Places data:", places); // 데이터 콘솔에 출력

    places.forEach(function(place, index) {
        var markerPosition = new kakao.maps.LatLng(place.y, place.x);
        var marker = new kakao.maps.Marker({
            position: markerPosition
        });
        markers.push(marker);

        var itemEl = getListItem(index, place); // 아이템 생성
        fragment.appendChild(itemEl);
        bounds.extend(markerPosition); // 마커의 위치를 bounds에 추가

        // 이벤트 리스너 설정
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

    listEl.appendChild(fragment);
    menuEl.scrollTop = 0;
    map.setBounds(bounds); // 모든 마커를 포함하는 범위로 지도 범위 조정
}

// 검색결과 항목을 Element로 반환하는 함수입니다
function getListItem(index, place) {
    var el = document.createElement('li'),
    itemStr = '<span class="markerbg marker_' + (index+1) + '"></span>' +
              '<div class="info">' +
              '<h5>' +  place.place_name + '</h5>' +
              '<span>' + '주소 : ' + place.address_name + '<br>' +
                        '도로명 주소 : ' + place.road_address_name + '<br>' +
                        '카테고리 : ' + place.category_name + '<br>' +
                        '장소 주소 : ' + place.place_url + '</span>' +
              '</div>';

    el.innerHTML = itemStr;
    el.className = 'item';

    return el;
}

// 지도 위에 표시되고 있는 마커를 모두 제거합니다
function removeMarker() {
    for ( var i = 0; i < markers.length; i++ ) {
        markers[i].setMap(null);
    }
    markers = [];
}

// 검색결과 목록 하단에 페이지번호를 표시는 함수입니다
function displayPagination(pagination) {
    var paginationEl = document.getElementById('pagination'),
        fragment = document.createDocumentFragment(),
        i;

    // 기존에 추가된 페이지번호를 삭제합니다
    while (paginationEl.hasChildNodes()) {
        paginationEl.removeChild (paginationEl.lastChild);
    }

    for (i=1; i<=pagination.last; i++) {
        var el = document.createElement('a');
        el.href = "#";
        el.innerHTML = i;

        if (i===pagination.current) {
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

 // 검색결과 목록의 자식 Element를 제거하는 함수입니다
function removeAllChildNods(el) {
    while (el.hasChildNodes()) {
        el.removeChild (el.lastChild);
    }
}

window.onload = loadData; // 페이지 로드 시 데이터 로드 함수 호출


// 마이페이지 버튼 클릭 시 로그인 여부 확인
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

                            // 마커를 클릭하면 인포윈도우를 표시합니다
                            kakao.maps.event.addListener(marker, 'click', function() {
                                infowindow.open(map, marker);
                            });

                            // 지도 클릭 시 인포윈도우 닫기
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
