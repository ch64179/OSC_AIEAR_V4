<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport"
            content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
        <title>KG이니시스 결제샘플</title>
		<!--테스트 JS-->
		<script language="javascript" type="text/javascript" src="https://stgstdpay.inicis.com/stdjs/INIStdPay.js" charset="UTF-8"></script>
    </head>

    <body class="wrap">
        <!-- 본문 -->
        <main class="col-8 cont" id="bill-01">
            <!-- 페이지타이틀 -->
            <section class="mb-5">
                <div class="tit">
                    <h2>일반결제</h2>
                    <p>KG이니시스 결제창을 호출하여 다양한 지불수단으로 안전한 결제를 제공하는 서비스</p>
                </div>
            </section>
            <!-- //페이지타이틀 -->


                <div class="card">
                    <div class="card_tit">
                        <h3>PC 일반결제</h3>
                    </div>

                    <!-- 유의사항 -->
                    <div class="card_desc">
                        <h4>※ 유의사항</h4>
                        <ul>
                            <li>테스트MID 결제시 실 승인되며, 당일 자정(24:00) 이전에 자동으로 취소처리 됩니다.</li>
							<li>가상계좌 채번 후 입금할 경우 자동환불되지 않사오니, 가맹점관리자 내 "입금통보테스트" 메뉴를 이용부탁드립니다.<br>(실 입금하신 경우 별도로 환불요청해주셔야 합니다.)</li>
							<li>국민카드 정책상 테스트 결제가 불가하여 오류가 발생될 수 있습니다. 국민, 카카오뱅크 외 다른 카드로 테스트결제 부탁드립니다.</li>
                        </ul>
                    </div>
                    <!-- //유의사항 -->


                    <form name="sendPayForm" id="sendPayForm" method="post">
                    	<input type="hidden" name="version" value="1.0">
                    	<input type="hidden" name="mid" value="${mid}">
                    	<input type="hidden" name="goodsname" value="테스트">
                    	<input type="hidden" name="oid" value="${oid}">
                    	<input type="hidden" name="price" value="${price}">
                    	<input type="hidden" name="currency" value="WON">
                    	<input type="hidden" name="buyername" value="TEST">
                    	<input type="hidden" name="buyertel" value="010-1234-5678">
                    	<input type="hidden" name="buyeremail" value="test@test.com">
                    	<input type="hidden" name="timestamp" value="${timestamp}">
                    	<input type="hidden" name="signature" value="${signature}">
                    	<input type="hidden" name="returnUrl" value="http://localhost:8080/inicis/payAfter.do?${_csrf.parameterName}=${_csrf.token}">
                    	<input type="hidden" name="closeUrl" value="http://localhost:8080/inicis/close?${_csrf.parameterName}=${_csrf.token}">
                    	<input type="hidden" name="mKey" value="${mKey}">
                    	<input type="hidden" name="gopaymethod" value="">
                    	<input type="hidden" name="offerPeriod" value="">
                    	<input type="hidden" name="acceptmethod" value="CARDPOINT:HPP(1):no_receipt:va_receipt:vbanknoreg(0):below1000">
                    	<input type="hidden" name="languageView" value="">
                    	<input type="hidden" name="charset" value="">
                    	<input type="hidden" name="payViewType" value="">
                    	<input type="hidden" name="quotabase" value="${cardQuotaBase}">
                    	<input type="hidden" name="ini_onnlycardcode" value="">
                    	<input type="hidden" name="ini_cardcode" value="">
                    	<input type="hidden" name="ansim_quota" value="">
                    	<input type="hidden" name="vbankRegNo" value="">
                    	<input type="hidden" name="merchantData" id="merchantData" value="">
                    </form>
				
				    <button id="btnPay" onclick="INIStdPay.pay('sendPayForm')" style="margin-top:50px">결제 요청</button>
                </div>
        </main>
    </body>
    
    <script>
    	var frm = document.sendPayForm;
    	frm.gopaymethod.value = "Card";
    	
    	document.getElementById("btnPay").click();
    </script>
</html>