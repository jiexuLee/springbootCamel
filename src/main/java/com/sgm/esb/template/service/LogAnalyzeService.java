package com.sgm.esb.template.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;

@Slf4j
public class LogAnalyzeService {

    public String queryLogDetail(Exchange exchange) throws Exception {
        String emailId = exchange.getIn().getHeader("emailId").toString();
        log.info("emailId >> {} ", emailId);
        return emailId;
    }

}
