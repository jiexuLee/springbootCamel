package com.sgm.esb.template;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = Application.class)
@UseAdviceWith
public class Test01 {
	private static boolean setUpIsDone = false;

	private static final String MOCK_VALIDATION_URI = "mock:validateRestBody";

	@Autowired
	private CamelContext context;

	@Autowired
	private ProducerTemplate template;

	@Before
	public void setUpClass() throws Exception {
		if (setUpIsDone) {
			return;
		}
		// do the setup
		setUpIsDone = true;

		/* Remove all sap related nodes */
		context.getRouteDefinition("restfulMainRoute").adviceWith(context, new AdviceWithRouteBuilder() {
			@Override
			public void configure() throws Exception {
				// weave the node in the route which has id = xxx and remove it
				weaveById("restBiz").replace().to(MOCK_VALIDATION_URI);
			}
		});

		context.start();
	}

	@Test
	public void testCase_TS01() throws Exception {
		NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();

		MockEndpoint mockEndpoint = context.getEndpoint(MOCK_VALIDATION_URI, MockEndpoint.class);
		mockEndpoint.reset();

		Exchange exchangeOut = template.request("direct:mainRoute", e -> {
			e.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
			e.setProperty("TEST-PROP", "testValue");
			e.setProperty(FuseConstants.KEY_EXCHANGE_PROPERTIES_SGMFUSE_LOGURI, "10.203.96.43:7005/esb/restapi/esblog/v1");
		});

		/* Validate the exchange received */
		String testOuptMsg = "Test Hello 01";
		mockEndpoint.whenAnyExchangeReceived(e -> e.getIn().setBody(testOuptMsg));

		mockEndpoint.expectedMessageCount(1);
		mockEndpoint.assertIsSatisfied();
		Exchange mockExchange = mockEndpoint.getExchanges().get(0);
		String loguri = mockExchange.getProperty(FuseConstants.KEY_EXCHANGE_PROPERTIES_SGMFUSE_LOGURI, String.class);

		assertTrue("LogURI not as expected", "10.203.96.43:7005/esb/restapi/esblog/v1".equals(loguri));

		boolean matches = notify.matches(5, TimeUnit.SECONDS);
		assertTrue(matches);

		System.out.println(exchangeOut.getOut().getBody(String.class));
	}

}
