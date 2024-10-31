// 마커를 클릭했을 때 해당 장소의 상세정보를 보여줄 커스텀오버레이입니다
var placeOverlay = new kakao.maps.CustomOverlay({zIndex:1}), 
    contentNode = document.createElement('div'), // 커스텀 오버레이의 컨텐츠 엘리먼트 입니다 
    markers = [], // 마커를 담을 배열입니다
    currCategory = ''; // 현재 선택된 카테고리를 가지고 있을 변수입니다
 
var mapContainer = document.getElementById('map'), // 지도를 표시할 div 
    mapOption = {
        center: new kakao.maps.LatLng(37.566826, 126.9786567), // 지도의 중심좌표
        level: 5 // 지도의 확대 레벨
    };

// 지도를 생성합니다    
var map = new kakao.maps.Map(mapContainer, mapOption);





// 전역 변수로 map과 manager 선언
var map;
var manager;

// 지도 초기화 함수
function initializeMap() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            var lat = position.coords.latitude,
                lon = position.coords.longitude;

            var locPosition = new kakao.maps.LatLng(lat, lon);

            // 지도 생성
            map = new kakao.maps.Map(document.getElementById('map'), {
                center: locPosition,
                level: 5
            });

            // Drawing Manager 옵션 설정
            var options = {
                map: map,
                drawingMode: [
                    kakao.maps.drawing.OverlayType.MARKER,
                ],
                guideTooltip: ['draw', 'drag', 'edit'],
                markerOptions: {
                    draggable: true,
                    removable: true
                },
                polylineOptions: {
                    draggable: true,
                    removable: true,
                    editable: true,
                    strokeColor: '#39f',
                    hintStrokeStyle: 'dash',
                    hintStrokeOpacity: 0.5
                }
            };

            // Drawing Manager 생성
            manager = new kakao.maps.drawing.DrawingManager(options);

            // 지도 이벤트 등록
            kakao.maps.event.addListener(map, 'idle', searchPlaces);

        }, function(error) {
            // 위치 정보를 가져오는데 실패한 경우 서울시청을 중심으로 설정
            var defaultPosition = new kakao.maps.LatLng(37.566826, 126.9786567);
            initializeWithPosition(defaultPosition);
        });
    } else {
        // geolocation을 사용할 수 없는 경우 서울시청을 중심으로 설정
        var defaultPosition = new kakao.maps.LatLng(37.566826, 126.9786567);
        initializeWithPosition(defaultPosition);
    }
}

// 위치 정보로 지도 초기화하는 함수
function initializeWithPosition(position) {
    map = new kakao.maps.Map(document.getElementById('map'), {
        center: position,
        level: 5
    });

    // Drawing Manager 옵션과 생성은 위와 동일하게 구현
    // ... (options 설정 코드)
    manager = new kakao.maps.drawing.DrawingManager(options);

    kakao.maps.event.addListener(map, 'idle', searchPlaces);
}

// 그리기 도구 선택 함수
function selectOverlay(type) {
    // 그리기 중이면 그리기를 취소합니다
    manager.cancel();

    // 클릭한 그리기 요소 타입을 선택하고 그리기 모드로 전환합니다
    manager.select(kakao.maps.drawing.OverlayType[type]);
}

// 페이지 로드 시 지도 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeMap();
});






// 장소 검색 객체를 생성합니다
var ps = new kakao.maps.services.Places(map);

// 검색 결과 목록이나 마커를 클릭했을 때 장소명을 표출할 인포윈도우를 생성합니다
var infowindow = new kakao.maps.InfoWindow({zIndex:1});



// 지도에 idle 이벤트를 등록합니다
kakao.maps.event.addListener(map, 'idle', searchPlaces);

// 커스텀 오버레이의 컨텐츠 노드에 css class를 추가합니다 
contentNode.className = 'placeinfo_wrap';

// 커스텀 오버레이의 컨텐츠 노드에 mousedown, touchstart 이벤트가 발생했을때
// 지도 객체에 이벤트가 전달되지 않도록 이벤트 핸들러로 kakao.maps.event.preventMap 메소드를 등록합니다 
addEventHandle(contentNode, 'mousedown', kakao.maps.event.preventMap);
addEventHandle(contentNode, 'touchstart', kakao.maps.event.preventMap);

// 커스텀 오버레이 컨텐츠를 설정합니다
placeOverlay.setContent(contentNode);  

// 각 카테고리에 클릭 이벤트를 등록합니다
addCategoryClickEvent();

// 엘리먼트에 이벤트 핸들러를 등록하는 함수입니다
function addEventHandle(target, type, callback) {
    if (target.addEventListener) {
        target.addEventListener(type, callback);
    } else {
        target.attachEvent('on' + type, callback);
    }
}

// 카테고리 검색을 요청하는 함수입니다
function searchPlaces() {
    if (!currCategory) {
        return;
    }
    
    // 커스텀 오버레이를 숨깁니다 
    placeOverlay.setMap(null);

    // 지도에 표시되고 있는 마커를 제거합니다
    removeMarker();

    // 서버로 모든 가맹점 데이터 요청
    fetch(`/api/v1/franchise?la=${map.getCenter().getLat()}&lo=${map.getCenter().getLng()}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.length > 0) {
                displayPlaces(data);
            } else {
                alert('검색 결과가 없습니다.');
            }
        })
        .catch(error => console.error('Error:', error));
}





// 장소검색이 완료됐을 때 호출되는 콜백함수 입니다
function placesSearchCB(data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) {

        // 정상적으로 검색이 완료됐으면 지도에 마커를 표출합니다
        displayPlaces(data);
    } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
        // 검색결과가 없는경우 해야할 처리가 있다면 이곳에 작성해 주세요

    } else if (status === kakao.maps.services.Status.ERROR) {
        // 에러로 인해 검색결과가 나오지 않은 경우 해야할 처리가 있다면 이곳에 작성해 주세요
        
    }
}

// 지도에 마커를 표출하는 함수입니다
function displayPlaces(places) {
    // 마커를 제거합니다
    removeMarker();

    // 마커를 표시합니다
    for (var i = 0; i < places.length; i++) {
        // 위도와 경도로 마커를 생성하고 지도에 표시합니다
        var position = new kakao.maps.LatLng(places[i].latitude, places[i].longitude);
        var marker = addMarker(position, 0); // 카테고리 인덱스를 0으로 설정 (예시)

        // 마커와 검색결과 항목을 클릭 했을 때 장소정보를 표출하도록 클릭 이벤트를 등록합니다
        (function(marker, place) {
            kakao.maps.event.addListener(marker, 'click', function() {
                displayPlaceInfo(place);
            });
        })(marker, places[i]);
    }
}

// 마커를 생성하고 지도 위에 마커를 표시하는 함수입니다
function addMarker(position, order) {
    var imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/places_category.png', // 마커 이미지 url, 스프라이트 이미지를 씁니다
        imageSize = new kakao.maps.Size(27, 28),  // 마커 이미지의 크기
        imgOptions =  {
            spriteSize : new kakao.maps.Size(72, 208), // 스프라이트 이미지의 크기
            spriteOrigin : new kakao.maps.Point(46, (order*36)), // 스프라이트 이미지 중 사용할 영역의 좌상단 좌표
            offset: new kakao.maps.Point(11, 28) // 마커 좌표에 일치시킬 이미지 내에서의 좌표
        },
        markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions),
            marker = new kakao.maps.Marker({
            position: position, // 마커의 위치
            image: markerImage 
        });

    marker.setMap(map); // 지도 위에 마커를 표출합니다
    markers.push(marker);  // 배열에 생성된 마커를 추가합니다

    return marker;
}

// 지도 위에 표시되고 있는 마커를 모두 제거합니다
function removeMarker() {
    for ( var i = 0; i < markers.length; i++ ) {
        markers[i].setMap(null);
    }   
    markers = [];
}

// 클릭한 마커에 대한 장소 상세정보를 커스텀 오버레이로 표시하는 함수입니다
function displayPlaceInfo(place) {
    var searchQuery = encodeURIComponent(place.name + " " + (place.address || "")); // 검색어 생성
    var content = '<div class="placeinfo">' +
                    '   <a class="title" href="https://map.kakao.com/?q=' + searchQuery + '" target="_blank" title="' + place.name + '">' + place.name + '</a>';

    // sector와 road_address 추가
    if (place.mapAddress) {
        content += '    <span title="' + place.mapAddress + '">상세 주소: ' + place.mapAddress + '</span>';
    }

    if (place.sector) {
        content += '    <span title="' + place.sector + '">업종: ' + place.sector + '</span>';
    }

    // 평점 버튼 추가
    content += '<span class="tel">' + place.phone + '</span>' +
               // 버튼을 감싸는 div 추가하여 가운데 정렬
               '<div class="text-center" style="margin-top: 10px;">' +
                   '<button class="btn btn-outline-danger rating-button" onclick="ratePlace(\'' + place.name + '\')" style="border-radius: 10px;">평점 남기기</button>' +
               '</div>' +
               '</div>' +
               '<div class="after"></div>';

    contentNode.innerHTML = content; // 커스텀 오버레이의 내용을 설정
    placeOverlay.setPosition(new kakao.maps.LatLng(place.latitude, place.longitude)); // 오버레이의 위치 설정
    placeOverlay.setMap(map); // 오버레이를 지도에 표시
}



// 각 카테고리에 클릭 이벤트를 등록합니다
function addCategoryClickEvent() {
    var specificCategory = document.getElementById('BK9'); // 특정 li 요소를 선택
    specificCategory.onclick = onClickCategory; // 해당 요소에 클릭 이벤트 등록
}

// 카테고리를 클릭했을 때 호출되는 함수입니다
function onClickCategory() {
    var id = this.id,
        className = this.className;

    placeOverlay.setMap(null);

    if (className === 'on') {
        currCategory = '';
        changeCategoryClass();
        removeMarker();
    } else {
        currCategory = id;
        changeCategoryClass(this);
        searchPlaces();
    }
}

// 클릭된 카테고리에만 클릭된 스타일을 적용하는 함수입니다
function changeCategoryClass(el) {
    var category = document.getElementById('category'),
        children = category.children,
        i;

    for ( i=0; i<children.length; i++ ) {
        children[i].className = '';
    }

    if (el) {
        el.className = 'on';
    } 
}



// 초기화면 내 위치로 설정
if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function(position) {
        var lat = position.coords.latitude, // 위도
            lon = position.coords.longitude; // 경도

        var locPosition = new kakao.maps.LatLng(lat, lon); // 현재 위치
        var mapOption = {
            center: locPosition, // 현재 위치를 중심으로 지도 설정
            level: 5 // 확대 레벨
        };

        // 지도 생성
        map = new kakao.maps.Map(document.getElementById('map'), mapOption);

        // idle 이벤트 등록 (지도가 움직일 때마다 검색 실행)
        kakao.maps.event.addListener(map, 'idle', searchPlaces);

    }, function(error) {
        console.error("Error Code = " + error.code + " - " + error.message);
        // 사용자의 위치를 못 가져왔을 때의 기본 지도 설정 (서울시청 중심)
        var mapOption = {
            center: new kakao.maps.LatLng(37.566826, 126.9786567), // 서울시청 좌표
            level: 5 // 확대 레벨
        };

        map = new kakao.maps.Map(document.getElementById('map'), mapOption);

        // idle 이벤트 등록 (지도가 움직일 때마다 검색 실행)
        kakao.maps.event.addListener(map, 'idle', searchPlaces);
    });
} else {
    // 사용자의 위치를 지원하지 않는 경우 기본 위치 설정 (서울시청)
    var mapOption = {
        center: new kakao.maps.LatLng(37.566826, 126.9786567), // 서울시청 좌표
        level: 3 // 확대 레벨
    };

    map = new kakao.maps.Map(document.getElementById('map'), mapOption);

    // idle 이벤트 등록 (지도가 움직일 때마다 검색 실행)
    kakao.maps.event.addListener(map, 'idle', searchPlaces);
}


// 리뷰

// 평점 버튼 클릭 시 모달을 여는 함수
function ratePlace(placeName) {
    showReviewModal(placeName); // showReviewModal 호출
}


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

// DOMContentLoaded 이벤트 리스너
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
        event.preventDefault();
        const rating = ratingInput.value / 2; // 별점
        const reviewText = reviewTextElement.value; // 리뷰 내용
        const files = imageUploadElement.files; // 업로드된 파일
        const fileName = files.length > 0 ? files[0].name : "No file uploaded"; // 파일 이름

        console.log("등록 정보:", {
            상호명: currentFranchiseName,
            별점: rating,
            리뷰내용: reviewText,
            파일명: fileName
        });

        const reader = new FileReader();
        reader.onloadend = function() {
            const base64Image = reader.result; // 이미지를 Base64로 변환
            saveReview(currentFranchiseName, rating, reviewText, base64Image);
            $('#reviewModal').modal('hide');
            alert("등록이 완료되었습니다.");
        };

        if (files.length > 0) {
            reader.readAsDataURL(files[0]);
        } else {
            saveReview(currentFranchiseName, rating, reviewText, null);
            $('#reviewModal').modal('hide');
            alert("등록이 완료되었습니다.");
        }
    });
});

// 리뷰 저장 함수
function saveReview(franchiseName, rating, reviewText, base64Image) {
    var review = {
        franchiseName: franchiseName,
        rating: rating,
        reviewText: reviewText,
        image: base64Image
    };

    var reviews = JSON.parse(localStorage.getItem('reviews')) || [];
    reviews.push(review);
    localStorage.setItem('reviews', JSON.stringify(reviews));

    console.log("리뷰 저장:", review);
}



// 프로필 함수
document.addEventListener('DOMContentLoaded', function() {
    const profileButton = document.getElementById('btnProfile');
    const profileLayer = document.getElementById('profileLayer');
    const loginButton = document.getElementById('loginButton');
    const nickName = document.querySelector('.tit_name[data-id="nickName"]');
    const profileImg = document.querySelector('.thumb_profile img[data-id="profileImg"]');
    const accessToken = localStorage.getItem('accessToken');
    const username = localStorage.getItem('username');

    // 로그인 상태 확인 및 UI 업데이트
    if (accessToken && username) {

        // 로그인 된 상태
        nickName.textContent = username;
        loginButton.classList.add('d-none');
        profileButton.classList.remove('d-none');

        // 프로필 버튼 클릭 이벤트
        profileButton.addEventListener('click', function(event) {
            event.preventDefault(); // 기본 클릭 이벤트 방지
            profileLayer.style.display = profileLayer.style.display === 'none' ? 'block' : 'none'; // 보이기/숨기기 토글
        });

        // 문서의 다른 부분을 클릭했을 때 프로필 창 숨기기
        document.addEventListener('click', function(event) {
            if (!profileButton.contains(event.target) && !profileLayer.contains(event.target)) {
                profileLayer.style.display = 'none'; // 프로필 창 숨기기
            }
        });

    } else {
        // 로그인 되지 않은 상태
        loginButton.classList.remove('d-none');
        profileButton.classList.add('d-none');

        loginButton.addEventListener('click', function(event) {
            // 로그인 페이지로 이동
            window.location.href = 'loginPage.html';
        });
    }
});









