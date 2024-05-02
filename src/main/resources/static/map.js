var mapContainer = document.getElementById('map'), // 지도를 표시할 div
              mapOption = {
                  center: new kakao.maps.LatLng(36.2683, 127.6358), // 지도의 중심좌표
                  level: 14 // 지도의 확대 레벨
              };

          var map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다

          // 마커 클러스터러를 생성합니다
          var clusterer = new kakao.maps.MarkerClusterer({
              map: map, // 마커들을 클러스터로 관리하고 표시할 지도 객체
              averageCenter: true, // 클러스터에 포함된 마커들의 평균 위치를 클러스터 마커 위치로 설정
              minLevel: 10 // 클러스터 할 최소 지도 레벨
          });


          function loadData() {
              var url = '/franchise?fromLa=37.0&toLa=38.0&fromLo=126.0&toLo=128.0';
              fetch(url)
                  .then(response => response.json())
                  .then(data => {
                      var markers = data.map(function (franchise) {
                          var markerPosition = new kakao.maps.LatLng(franchise.latitude, franchise.longitude);
                          var marker = new kakao.maps.Marker({
                              position: markerPosition
                          });

                          // 인포윈도우에 표시될 내용 설정
                          var infowindow = new kakao.maps.InfoWindow({
                              content: '<div style="padding:20px;">' +
                                       '상호명: ' + franchise.name + '<br>' +
                                       '업종: ' + franchise.sector + '<br>' +
                                       '주소: ' + franchise.map_address + '</div>',
                                removable : true
                          });

                          // 마커에 클릭 이벤트를 등록합니다
                          kakao.maps.event.addListener(marker, 'click', function() {
                              infowindow.open(map, marker); // 마커 클릭 시 인포윈도우를 표시
                          });

                          return marker;
                      });
                      // 클러스터러에 마커들을 추가합니다
                      clusterer.addMarkers(markers);
                  })
                  .catch(err => console.error('Error loading the franchise data:', err));
          }
          window.onload = loadData; // 페이지 로드 시 데이터 로드