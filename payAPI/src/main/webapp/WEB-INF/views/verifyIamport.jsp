<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>

<meta charset="UTF-8">
<!-- 아래 제이쿼리는 1.0이상이면 원하는 버전을 사용하셔도 무방합니다. -->
<script type="text/javascript"
	src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
<!-- 아임포트 api 
결제가 필요한 곳에 아임 포트의 한 줄 자바스크립트 라이브러리를 추가해줘야 합니다. 이 라이브러리를 통해 window.IMP변수에 접근이 가능해집니다.-->
<script type="text/javascript"
	src="https://cdn.iamport.kr/js/iamport.payment-1.1.8.js"></script>
<script type="text/javascript">
	//api 스크립트
	//여기서 중요하게 볼 부분은 IMP.init과 PG입니다. 
	//IMP.init에 들어갈 인자는 위에서 복사한 가맹점 식별코드를 넣는 것이며, PG는 또한 위에서 설정한 원하는 PG사를 넣어주시면 됩니다.
	function iamport() {
		//가맹점 식별코드
		IMP.init('imp80268540');
		IMP.request_pay({
			// 테스트 발급받은 종류 기입
			pg : 'kakaopay',
			pay_method : 'card',
			merchant_uid : 'merchant_' + new Date().getTime(),
			name : '상품1', //결제창에서 보여질 이름
			amount : 100, //실제 결제되는 가격
			buyer_email : 'iamport@siot.do',
			buyer_name : '구매자이름',
			buyer_tel : '010-1234-5678',
			buyer_addr : '서울 강남구 도곡동',
			buyer_postcode : '123-456',
        	// 아래 부분을 추가해줍니다 - 결제 진행시  페이지를 이동(redirect 시키는 특징)
    		//모바일에서 페이지가 넘어가 검증이 불가능한 상황 완료시 callback 응답을 받을수 없는 상태가 되는것
    		//아래 소스 추가하면 모바일 화면에서는 콜백 함수가 호출되는 것이 아닌, 해당 url만 호출하게 됩니다.
    		//localhost 를 내 ip 주소로 바꿔준다
	    m_redirect_url : 'http://localhost:80/orderCompleteMobile'
        		<!-- -->
		}, function(rsp) {
			console.log(rsp);
			// 결제검증
			$.ajax({
	        	type : "POST",
	        	url : "/verifyIamport/" + rsp.imp_uid 
	        }).done(function(data) {
	        	//크롬 콘솔창에 나옴
	        	console.log(data);
	        	
	        	// 위의 rsp.paid_amount 와 data.response.amount를 비교한후 로직 실행 (import 서버검증)
	        	if(rsp.paid_amount == data.response.amount){
		        	alert("결제 및 결제검증완료");
	        	} else {
	        		alert("결제 실패");
	        	}
	        });
		});

	}

	$(function() {
		$("#pay").click(function() {
			iamport();
		})
	})
</script>
<title>결제</title>
</head>
<body>
	<!-- 아이디,이름 등 입력받아 넘길예정 -->
	<form action="" method="post">
		<div class="form-group">
			<label for="uid">아이디</label> <input name="uid" id="uid"
				class="form-control" required="required">
		</div>


	</form>
	<button id="pay">결제</button>
</body>
</html>