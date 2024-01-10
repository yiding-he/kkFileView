package com.bsteam.bsm.channel;

import cn.keking.model.ChannelParameters;
import cn.keking.utils.Jackson;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

class BsmChannelTest {

  @Test
  public void testBuildParameters() throws Exception {
    ChannelParameters parameters = new ChannelParameters();
    parameters.setChannel("bsm");
    parameters.set("env", "api");
    parameters.set("project_id", "2023071100000002963");
    parameters.set("check_building_id", "2023071900000005429");
    parameters.set("report_type", "1");
    var json = Jackson.serializeStandardJson(parameters);
    var base64 = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    System.out.println("base64 = " + base64);
  }
}
