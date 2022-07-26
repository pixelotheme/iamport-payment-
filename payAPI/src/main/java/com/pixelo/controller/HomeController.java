package com.pixelo.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	private IamportClient api;
	
	public HomeController() {
		//토큰 발급을 위해 키를 생성함
    	// REST API 키와 REST API secret 를 아래처럼 순서대로 입력한다.
		this.api = new IamportClient("1554887615622635","l2u67J8gBrtFDDC06I59uRw7dwqCBXeVhuUndlirGXDcVFEgtClCBGvmZeDIUOLSJa0XaEDUo3mXGUO5");
	}
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}


	//paymentByImpUid 함수를 사용하기 위해 토큰이 필요함
	@ResponseBody
	@RequestMapping(value="/verifyIamport/{imp_uid}", method = RequestMethod.POST)
	public IamportResponse<Payment> paymentByImpUid(Model model, Locale locale, HttpSession session
			, @PathVariable(value= "imp_uid") String imp_uid) throws IamportResponseException, IOException{	
			
		//paymentByImpUid 함수는 아임포트서버에서 imp_uid(거래 고유번호)를 검사하여, 데이터를 보내줍니다.
		return api.paymentByImpUid(imp_uid);
	}
	
	//모바일 접속시 들어오는 컨트롤러
	@RequestMapping(value="/orderCompleteMobile", produces = "application/text; charset=utf8", method = RequestMethod.GET)
	public String orderCompleteMobile(
			@RequestParam(required = false) String imp_uid
			, @RequestParam(required = false) String merchant_uid
			, Model model
			, Locale locale
			, HttpSession session) throws IamportResponseException, IOException
	{
		
		IamportResponse<Payment> result = api.paymentByImpUid(imp_uid);
		
		// 결제 가격과 검증결과를 비교한다.
		if(result.getResponse().getAmount().compareTo(BigDecimal.valueOf(100)) == 0) {
			System.out.println("검증통과");
		}
		
		return "redirect:verifyIamport";
	}
	
	//PG사를 통한 결제 승인 -> PG사 결괏값 서버로 보냄 -> 서버에서 아임포트 토큰 발행 
	//-> 해당 토큰으로 아임포트 내 결과값 확인 -> PG사 결과값과 아임포트 결과값 비교 -> 참이면 TRUE입니다.
	@GetMapping("/verifyIamport")
	public void verifyIamport() {
		
	}
}
