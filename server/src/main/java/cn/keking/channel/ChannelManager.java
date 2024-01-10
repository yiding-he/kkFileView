package cn.keking.channel;

import cn.keking.channel.impl.DefaultChannel;
import cn.keking.config.ChannelConfig;
import cn.keking.model.ChannelParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 管理可扩展的渠道。新增渠道只需要：
 * 1. 创建 Channel 接口的实现类；
 * 2. 保证这个实现类存在于 ApplicationContext 中。
 */
@Service
public class ChannelManager {

  @Autowired
  private List<Channel> channelList;

  @Autowired
  private DefaultChannel defaultChannel;

  @Autowired
  private ChannelConfig channelConfig;

  public Map<String, String> getChannelConfig(String channelName) {
    return channelConfig.getConfigs().getOrDefault(channelName, Collections.emptyMap());
  }

  public Channel resolve(ChannelParameters parameters) {
    if (channelList == null) {
      return defaultChannel;
    }

    var channelName = parameters.getChannel();
    parameters.setConfig(getChannelConfig(channelName));

    return channelList.stream()
      .filter(channel -> Objects.equals(channel.getName(), channelName))
      .findFirst()
      .orElse(defaultChannel);
  }
}
