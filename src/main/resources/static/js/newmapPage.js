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



       // 지도 초기화 시 이벤트 리스너 수정
       function initializeMap() {
           if (navigator.geolocation) {
               navigator.geolocation.getCurrentPosition(function (position) {
                   const lat = position.coords.latitude,
                       lon = position.coords.longitude;

                   const locPosition = new kakao.maps.LatLng(lat, lon);

                   // 지도 생성
                   map = new kakao.maps.Map(document.getElementById('map'), {
                       center: locPosition,
                       level: 5
                   });

                   // Drawing Manager 생성
                   initializeDrawingManager();

                   // 초기 데이터 로드
                   loadInitialData();

                   // 지도 이벤트 등록 - debounce 적용
                   let timer;
                   kakao.maps.event.addListener(map, '', function () {
                       clearTimeout(timer);
                       timer = setTimeout(() => {
                           updateMapWithCachedData();
                       }, 300);
                   });

               }, function (error) {
                   const defaultPosition = new kakao.maps.LatLng(37.566826, 126.9786567);
                   initializeWithPosition(defaultPosition);
               });
           } else {
               const defaultPosition = new kakao.maps.LatLng(37.566826, 126.9786567);
               initializeWithPosition(defaultPosition);
           }
       }

       // 초기 데이터 로드 (서버 요청은 한 번만 수행)
       function loadInitialData() {
           fetchDataFromServer().then(data => {
               cachedData = processAndCacheData(data); // 데이터 캐싱
               initialDataLoaded = true;
               updateMapWithCachedData(); // 처음에 캐시된 데이터로 지도 표시
           });
       }

       // 캐시된 데이터로 지도 업데이트 (서버 요청 없음)
       function updateMapWithCachedData() {
           if (!initialDataLoaded) return; // 초기 데이터가 로드되지 않은 경우 실행하지 않음

           const currentBounds = map.getBounds(); // 현재 지도 영역 가져오기
           const visibleData = filterVisibleData(cachedData, currentBounds); // 현재 지도 영역에 해당하는 데이터 필터링
           updateMarkers(visibleData); // 마커 업데이트
       }

//       // 서버에서 데이터 가져오기
//       function fetchDataFromServer() {
//           return fetch('/api/v1/franchises')
//               .then(response => {
//                   if (!response.ok) {
//                       throw new Error(`HTTP error! status: ${response.status}`);
//                   }
//                   return response.json();
//               })
//               .catch(error => {
//                   console.error('Error fetching data from server:', error);
//                   return [];
//               });
//       }

       // 지도에 표시할 데이터 필터링
       function filterVisibleData(data, bounds) {
           const sw = bounds.getSouthWest();
           const ne = bounds.getNorthEast();

           return Array.from(data.values()).filter(item => {
               const lat = parseFloat(item.latitude);
               const lng = parseFloat(item.longitude);
               return lat >= sw.getLat() && lat <= ne.getLat() && lng >= sw.getLng() && lng <= ne.getLng();
           });
       }



// Drawing Manager 이벤트 처리를 위한 함수 수정
function initializeDrawingManager() {
   console.log("initializeDrawingManager 호출됨");

   var options = {
       map: map,
       drawingMode: [
           kakao.maps.drawing.OverlayType.MARKER
       ],
       markerOptions: {
           draggable: true,
           removable: true
       }
   };

   // Drawing Manager 생성
   drawingManager = new kakao.maps.drawing.DrawingManager(options);
   console.log("Drawing Manager 생성 완료");

   // 마커가 그려질 때 이벤트 등록
   kakao.maps.event.addListener(drawingManager, 'drawend', function(data) {
       console.log("drawend 이벤트 발생");
       console.log("drawend 이벤트 데이터:", data); // 이벤트 데이터 확인

       // OverlayType과 비교할 때 kakao.maps.drawing.OverlayType.MARKER 사용
       if (data.overlayType === kakao.maps.drawing.OverlayType.MARKER) {
           var position = data.target.getPosition();
           var lat = position.getLat();
           var lng = position.getLng();

           console.log(`마커 위치: 위도 ${lat}, 경도 ${lng}`);
           console.log("showMarkerInfoModal 호출 준비 중");

           // 주소를 가져오기 위해 geocoder 사용
           var geocoder = new kakao.maps.services.Geocoder();
           geocoder.coord2Address(lng, lat, function(result, status) {
               if (status === kakao.maps.services.Status.OK) {
                   var address = result[0].road_address ? result[0].road_address.address_name : result[0].address.address_name;
                   console.log("주소:", address);

                   // 마커 정보 입력 모달 표시
                   showMarkerInfoModal(lat, lng, data.target, address);
               } else {
                   console.log("주소를 가져오지 못했습니다.");
                   showMarkerInfoModal(lat, lng, data.target, "");
               }
           });
       } else {
           console.log("마커가 아닌 다른 타입의 객체가 그려짐:", data.overlayType);
       }
   });
}

// 마커 정보 입력 모달을 표시하는 함수
function showMarkerInfoModal(lat, lng, marker, address) {
    console.log("showMarkerInfoModal 호출됨");

    // 기존 모달이 있다면 제거
    var existingModal = document.getElementById('markerInfoModal');
    if (existingModal) {
        existingModal.remove();
    }

    // 모달 HTML 생성
    var modalHtml = `
    <div class="modal fade" id="markerInfoModal" tabindex="-1" aria-labelledby="markerInfoModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="markerInfoModalLabel">가맹점 정보 입력</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="markerInfoForm">
                        <div class="mb-3">
                            <label for="storeName" class="form-label">가맹점명</label>
                            <input type="text" class="form-control" id="storeName" required>
                        </div>
                        <div class="mb-3">
                            <label for="storeAddress" class="form-label">주소</label>
                            <input type="text" class="form-control" id="storeAddress" value="${address}" required>
                        </div>
                        <div class="mb-3">
                            <label for="storeSector" class="form-label">업종</label>
                            <input type="text" class="form-control" id="storeSector" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">사용 가능 지역화폐</label><br>
                            <div class="btn-group" role="group" aria-label="지역화폐 선택 버튼 그룹">

                                <input type="radio" class="btn-check" name="currencyType" id="currencyPaper" value="paper" autocomplete="off">
                                <label class="btn btn-outline-success" for="currencyPaper">지류</label>

                                <input type="radio" class="btn-check" name="currencyType" id="currencyMobile" value="mobile" autocomplete="off">
                                <label class="btn btn-outline-success" for="currencyMobile">모바일</label>

                                <input type="radio" class="btn-check" name="currencyType" id="currencyCard" value="card" autocomplete="off">
                                <label class="btn btn-outline-success" for="currencyCard">카드</label>

                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                    <button type="button" class="btn btn-primary" onclick="saveMarkerInfo(${lat}, ${lng})">저장</button>
                </div>
            </div>
        </div>
    </div>
    `;

    // 모달을 body에 추가
    document.body.insertAdjacentHTML('beforeend', modalHtml);
    console.log("모달 HTML이 body에 추가됨");

    // 모달 인스턴스 생성 및 표시
    try {
        var modal = new bootstrap.Modal(document.getElementById('markerInfoModal'));
        modal.show();
        console.log("모달 표시됨");
    } catch (error) {
        console.error("모달 표시 중 오류 발생:", error);
    }

    // 모달이 닫힐 때 이벤트 처리
    document.getElementById('markerInfoModal').addEventListener('hidden.bs.modal', function () {
        // 저장되지 않은 마커는 제거
        if (!marker.getMap()) {
            marker.setMap(null);
        }
    });
}


// 마커 정보를 저장하는 함수
function saveMarkerInfo(lat, lng) {
    const storeNameElement = document.getElementById('storeName');
    const storeAddressElement = document.getElementById('storeAddress');
    const storeSectorElement = document.getElementById('storeSector');

    // 요소가 존재하는지 확인
    if (!storeNameElement || !storeAddressElement || !storeSectorElement) {
        alert('필수 입력 요소가 누락되었습니다.');
        return;
    }

    const storeName = storeNameElement.value;
    const storeAddress = storeAddressElement.value;
    const storeSector = storeSectorElement.value;

    // 필수 정보 체크
    if (!storeName || !storeAddress || !storeSector) {
        alert('필수 정보를 모두 입력해주세요.');
        return;
    }

    // 체크박스 값 가져오기
    const currencyPaper = document.getElementById('currencyPaper').checked;
    const currencyMobile = document.getElementById('currencyMobile').checked;
    const currencyCard = document.getElementById('currencyCard').checked;

    // 사용자가 선택한 지역화폐 종류를 배열로 저장
    const currencies = [];
    if (currencyPaper) currencies.push('지류');
    if (currencyMobile) currencies.push('모바일');
    if (currencyCard) currencies.push('카드');

    // 마커 정보 객체 생성
    const markerInfo = {
        name: storeName,
        address: storeAddress,
        sector: storeSector,
        latitude: lat,
        longitude: lng,
        currencies: currencies // 선택된 지역화폐를 배열로 저장
    };

    // 토큰 확인 로그 추가
    const accessToken = localStorage.getItem('accessToken');
    console.log("Access Token:", accessToken);  // 토큰 값 출력

    // 토큰이 없거나 잘못된 경우 처리
    if (!accessToken) {
        console.error("Error: No access token found in localStorage.");
        alert("로그인 토큰이 존재하지 않습니다.");
        return;
    }

    console.log("저장할 마커 정보:", markerInfo);

    // 서버에 API 호출하여 데이터 저장
    fetch('/api/v1/member/franchise', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + accessToken
        },
        body: JSON.stringify(markerInfo)
    })
    .then(response => {
        console.log("서버 응답 상태:", response.status);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        alert(' 신규 장소가 성공적으로 등록되었습니다.\n 관리자의 검토 후 승인 절차가 완료되면 지도에 표시됩니다.\n 감사합니다.');
        const modal = bootstrap.Modal.getInstance(document.getElementById('markerInfoModal'));
        modal.hide();
        searchPlaces(); // 새로 고침 또는 추가된 마커 표시
    })
    .catch(error => {
        console.error('Error:', error);
        alert('가맹점 등록에 실패했습니다.');
    });
}



// 위치 정보로 지도 초기화하는 함수 수정
//function initializeWithPosition(position) {
//    map = new kakao.maps.Map(document.getElementById('map'), {
//        center: position,
//        level: 3
//    });
//
//    // Drawing Manager 초기화
//    initializeDrawingManager();
//
//    // 이벤트 리스너 등록
//    kakao.maps.event.addListener(map, 'idle', searchPlaces);
//}



// 그리기 도구 선택 함수
function selectOverlay(type) {
    if (!drawingManager) {
        console.error('Drawing Manager not initialized');
        return;
    }

    // 그리기 중이면 그리기를 취소합니다
    drawingManager.cancel();

    // 클릭한 그리기 요소 타입을 선택합니다
    drawingManager.select(kakao.maps.drawing.OverlayType[type]);
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeMap();
    addCategoryClickEvent(); // 카테고리 클릭 이벤트 등록
});

// Drawing Manager로 그려진 객체 정보 받기
function getDataFromDrawingManager() {
    // 그려진 객체 정보 가져오기
    var data = drawingManager.getData();
    console.log('그려진 객체 정보:', data);
}




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

let cachedFranchises = new Map(); // 가맹점 데이터를 저장할 캐시
let lastFetchTime = null;         // 마지막으로 API를 호출한 시간
const CACHE_DURATION = 5 * 60 * 1000; // 캐시 유효시간 (5분)
const DISTANCE_THRESHOLD = 0.01;  // 새로운 데이터를 요청할 거리 기준 (약 1km)

function searchPlaces() {
    if (!currCategory) {
        return;
    }

    // 커스텀 오버레이를 숨깁니다
    placeOverlay.setMap(null);

    const currentCenter = map.getCenter();
    const currentPosition = {
        lat: currentCenter.getLat(),
        lng: currentCenter.getLng()
    };

    // 캐시 사용 여부 결정
    if (shouldFetchNewData(currentPosition)) {
        // 서버에서 새 데이터 요청
        fetchFranchiseData(currentPosition);
    } else {
        // 캐시된 데이터 사용
        displayPlaces(Array.from(cachedFranchises.values()));
    }
}

function shouldFetchNewData(currentPosition) {
    // 캐시가 없거나 만료된 경우
    if (!lastFetchTime || Date.now() - lastFetchTime > CACHE_DURATION) {
        return true;
    }

    // 이전 위치에서 많이 벗어난 경우
    if (hasMovedSignificantly(currentPosition)) {
        return true;
    }

    return false;
}

function hasMovedSignificantly(currentPosition) {
    if (!lastFetchedPosition) return true;  // 첫 요청인 경우

    // 위도/경도 차이 계산
    const latDiff = Math.abs(currentPosition.lat - lastFetchedPosition.lat);
    const lngDiff = Math.abs(currentPosition.lng - lastFetchedPosition.lng);

    // 설정한 임계값보다 많이 이동했는지 확인
    return latDiff > DISTANCE_THRESHOLD || lngDiff > DISTANCE_THRESHOLD;
}

let lastFetchedPosition = null;

function fetchFranchiseData(position) {
     // 지도에 표시되고 있는 마커를 제거
     removeMarker();

     // URL 파라미터 유효성 검사 추가
     if (!position || !position.lat || !position.lng) {
         console.error('Invalid position data');
         return;
     }

     fetch(`/api/v1/franchise?la=${position.lat}&lo=${position.lng}`)
         .then(response => {
             if (!response.ok) {
                 throw new Error(`HTTP error! status: ${response.status}`);
             }
             return response.json();
         })
         .then(data => {
             // 데이터 유효성 검사
             if (!Array.isArray(data)) {
                 throw new Error('Invalid data format received');
             }

             // 캐시 업데이트
             cachedFranchises.clear();

             if (data.length > 0) {
                 data.forEach(franchise => {
                     cachedFranchises.set(franchise.id, franchise);
                 });
                 lastFetchTime = Date.now();
                 lastFetchedPosition = position;

                 displayPlaces(data);
             } else {
                 // 사용자 친화적인 메시지 표시
                 const message = `현재 위치 (${position.lat.toFixed(4)}, ${position.lng.toFixed(4)}) 근처에서
                                검색된 프랜차이즈가 없습니다. 다른 위치를 시도해보세요.`;
                 alert(message);

                 // 이전 캐시된 데이터가 있다면 표시 여부를 사용자에게 물어보기
                 if (cachedFranchises.size > 0) {
                     const showCached = confirm('이전에 검색된 결과를 보시겠습니까?');
                     if (showCached) {
                         displayPlaces(Array.from(cachedFranchises.values()));
                     }
                 }
             }
         })
         .catch(error => {
             console.error('Error:', error);

             // 네트워크 오류 발생 시 캐시된 데이터 활용
             if (cachedFranchises.size > 0) {
                 const showCached = confirm('네트워크 오류가 발생했습니다. 캐시된 데이터를 보시겠습니까?');
                 if (showCached) {
                     displayPlaces(Array.from(cachedFranchises.values()));
                 }
             } else {
                 alert('데이터를 불러오는데 실패했습니다. 잠시 후 다시 시도해주세요.');
             }
         });
 }



// 카테고리 검색 시 마커를 표출하는 함수입니다
function displayPlaces(places) {
    removeMarker();

    for (var i = 0; i < places.length; i++) {
        var position = new kakao.maps.LatLng(places[i].latitude, places[i].longitude);
        var marker = addMarker(position, 0, false);  // 카테고리 검색 마커 생성

        (function(marker, place) {
            kakao.maps.event.addListener(marker, 'click', function() {
                displayPlaceInfo(place);
            });
        })(marker, places[i]);
    }
}

// 마커를 생성하고 지도 위에 마커를 표시하는 함수입니다
function addMarker(position, order, isSearchResult = false) {
    var imageSrc, imageSize, imgOptions;

    if (isSearchResult) {
        // 검색 결과용 마커 이미지 설정
        imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png';
        imageSize = new kakao.maps.Size(36, 37);
        imgOptions = {
            spriteSize : new kakao.maps.Size(36, 691),
            spriteOrigin : new kakao.maps.Point(0, (order * 46) + 10),
            offset: new kakao.maps.Point(13, 37)
        };
    } else {
        // 카테고리 검색용 마커 이미지 설정
        imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/places_category.png';
        imageSize = new kakao.maps.Size(27, 28);
        imgOptions = {
            spriteSize : new kakao.maps.Size(72, 208),
            spriteOrigin : new kakao.maps.Point(46, (order*36)),
            offset: new kakao.maps.Point(11, 28)
        };
    }

    var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions),
        marker = new kakao.maps.Marker({
            position: position,
            image: markerImage
        });

    marker.setMap(map);
    markers.push(marker);

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
    content += // 버튼을 감싸는 div 추가하여 가운데 정렬
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
    var markerCategory = document.getElementById('create_marker'); // 가맹점 등록 요소

    specificCategory.onclick = onClickCategory; // 해당 요소에 클릭 이벤트 등록
    // 가맹점 등록 요소에 클릭 이벤트 추가
    markerCategory.onclick = function() {
        this.classList.toggle('on');  // create_marker 요소의 'on' 클래스 토글
        selectOverlay('MARKER');      // 가맹점 등록 동작
    };
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
            level: 3 // 확대 레벨
        };

        // 지도 생성
        map = new kakao.maps.Map(document.getElementById('map'), mapOption);

        // idle 이벤트 등록 (지도가 움직일 때마다 검색 실행)
//        kakao.maps.event.addListener(map, 'idle', searchPlaces);

    }, function(error) {
        console.error("Error Code = " + error.code + " - " + error.message);
        // 사용자의 위치를 못 가져왔을 때의 기본 지도 설정 (서울시청 중심)
        var mapOption = {
            center: new kakao.maps.LatLng(37.7454814, 127.0233146), // 경민대학교 중심 좌표
            level: 3 // 확대 레벨
        };

        map = new kakao.maps.Map(document.getElementById('map'), mapOption);

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



// 프로필 함수@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
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



//장소 검색@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// 검색결과 항목을 Element로 반환하는 함수입니다
function getListItem(index, places) {
    var el = document.createElement('li'),
    itemStr = '<span class="markerbg marker_' + (index+1) + '"></span>' +
                '<div class="info">' +
                '   <h5>' + places.place_name + '</h5>';

    if (places.road_address_name) {
        itemStr += '    <span>' + places.road_address_name + '</span>' +
                    '   <span class="jibun gray">' +  places.address_name  + '</span>';
    } else {
        itemStr += '    <span>' +  places.address_name  + '</span>';
    }

    itemStr += '  <span class="tel">' + places.phone  + '</span>' +
                '</div>';

    el.innerHTML = itemStr;
    el.className = 'item';

    return el;
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

// 검색결과 목록 또는 마커를 클릭했을 때 호출되는 함수입니다
// 인포윈도우에 장소명을 표시합니다
function displayInfowindow(marker, title) {
    var content = '<div style="padding:5px;z-index:1;">' + title + '</div>';

    infowindow.setContent(content);
    infowindow.open(map, marker);
}

 // 검색결과 목록의 자식 Element를 제거하는 함수입니다
function removeAllChildNods(el) {
    while (el.hasChildNodes()) {
        el.removeChild (el.lastChild);
    }
}


var ps = new kakao.maps.services.Places();

// 장소검색을 요청하는 함수입니다
function searchPlace() {
    var keyword = document.getElementById('innerQuery').value;

    if (!keyword.replace(/^\s+|\s+$/g, '')) {
        alert('키워드를 입력해주세요!');
        return false;
    }

    // 장소검색 객체를 통해 키워드로 장소검색을 요청합니다
    ps.keywordSearch(keyword, placesSearchCB);
}

// 장소검색이 완료됐을 때 호출되는 콜백함수입니다
function placesSearchCB(data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) {
        // 정상적으로 검색이 완료됐으면
        // 검색 목록과 마커를 표출합니다
        displayPlacesOnSearch(data);

        // 페이지 번호를 표출합니다
        displayPagination(pagination);
    } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
        alert('검색 결과가 존재하지 않습니다.');
        return;
    } else if (status === kakao.maps.services.Status.ERROR) {
        alert('검색 중 오류가 발생했습니다.');
        return;
    }
}

// 검색 결과 목록과 마커를 표출하는 함수입니다
function displayPlacesOnSearch(places) {
    var listEl = document.getElementById('placesList'),
        menuEl = document.getElementById('menu_wrap'),
        fragment = document.createDocumentFragment(),
        bounds = new kakao.maps.LatLngBounds();

    // 검색 결과 목록에 추가된 항목들을 제거합니다
    removeAllChildNods(listEl);

    // 지도에 표시되고 있는 마커를 제거합니다
    removeMarker();

    for (var i = 0; i < places.length; i++) {
        // 마커를 생성하고 지도에 표시합니다
        var placePosition = new kakao.maps.LatLng(places[i].y, places[i].x),
            marker = addMarker(placePosition, i, true),  // 검색 결과 마커 생성
            itemEl = getListItem(i, places[i]);

        // 검색된 장소 위치를 기준으로 지도 범위를 재설정하기위해
        // LatLngBounds 객체에 좌표를 추가합니다
        bounds.extend(placePosition);

        // 마커와 검색결과 항목에 mouseover 했을때
        // 해당 장소에 인포윈도우에 장소명을 표시합니다
        (function(marker, title) {
            kakao.maps.event.addListener(marker, 'mouseover', function() {
                displayInfowindow(marker, title);
            });

            kakao.maps.event.addListener(marker, 'mouseout', function() {
                infowindow.close();
            });

            itemEl.onmouseover =  function () {
                displayInfowindow(marker, title);
            };

            itemEl.onmouseout =  function () {
                infowindow.close();
            };
        })(marker, places[i].place_name);

        fragment.appendChild(itemEl);
    }

    // 검색결과 항목들을 검색결과 목록 Element에 추가합니다
    listEl.appendChild(fragment);
    menuEl.scrollTop = 0;

    // 검색된 장소 위치를 기준으로 지도 범위를 재설정합니다
    map.setBounds(bounds);
}

//fetch('/api/franchises/map')
//    .then(response => response.json())
//    .then(result => {
//        if (result.response && Array.isArray(result.response)) {
//            result.response.forEach(franchise => {
//                if (franchise.lat && franchise.lot) {
//                    var markerPosition = new kakao.maps.LatLng(franchise.lat, franchise.lot);
//                    var marker = new kakao.maps.Marker({
//                        position: markerPosition,
//                        map: map
//                    });
//
//                    var iwContent = `
//                        <div>
//                            <strong>${franchise.emd_nm}</strong><br>
//                            읍면동명: ${franchise.emd_nm}
//                        </div>
//                    `;
//                    var infowindow = new kakao.maps.InfoWindow({
//                        content: iwContent
//                    });
//
//                    kakao.maps.event.addListener(marker, 'mouseover', function() {
//                        infowindow.open(map, marker);
//                    });
//                    kakao.maps.event.addListener(marker, 'mouseout', function() {
//                        infowindow.close();
//                    });
//                }
//            });
//        } else {
//            console.error('프랜차이즈 데이터가 올바르게 전달되지 않았습니다');
//        }
//    })
//    .catch(error => console.error('Error:', error));

