package com.sgm.esb.template.route;

import com.sgm.esb.template.config.ESBProperties;
import com.sgm.esb.template.service.LogAnalyzeService;
import com.sgm.esb.template.service.LogTest;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sgm.esb.fuse.core.annotation.Logger;
import com.sgm.esb.fuse.core.constants.LoggerConstants;

@Component
@Logger
public class Route extends RouteBuilder {

	private final ESBProperties esbProperties;

	@Autowired
	public Route(ESBProperties esbProperties) {
		this.esbProperties = esbProperties;
	}

	@Override
	public void configure() throws Exception {
		restConfiguration().component("jetty").contextPath("/rest").port(8086);

		//RESTful入口
		from(esbProperties.getRestful()).to("direct:mainRoute");

		//定时入口
		from(esbProperties.getQuartz()).to("direct:mainRoute");

		//邮件日志查询接口
		from(esbProperties.getRestUri())
				.setHeader("Access-Control-Allow-Origin", constant("*"))
				.to("direct:emailLogDetail").end();

		from("direct:emailLogDetail").routeId("emailLogDetail")
				.bean(LogAnalyzeService.class,"queryLogDetail")
				.end();

		//主流程
		from("direct:mainRoute").routeId("restfulMainRoute")
			.log("REST request body: ${body}")
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.setBody(constant("{\"message\": \"Hello world!\"}")).id("restBiz")
			.setProperty(LoggerConstants.CONTENT, constant("要记录的交易日志BODY"))
			.wireTap("seda:logtest");
		
	}

}
