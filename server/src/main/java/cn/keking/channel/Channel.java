package cn.keking.channel;

import cn.keking.model.ChannelParameters;
import cn.keking.model.FileAttribute;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

public interface Channel {

  /**
   * 根据当前类名获得对应的渠道名，例如
   * DefaultChannel -> default
   * Unknown -> unknown
   */
  default String getName() {
    var className = this.getClass().getSimpleName();
    if (className.contains("$")) {
      className = StringUtils.substringBefore(className, "$");
    }

    var channelName = className.endsWith("Channel") ?
      StringUtils.removeEnd(className, "Channel") : className;
    channelName = channelName.toLowerCase();
    return channelName;
  }

  FileAttribute resolve(ChannelParameters parameters, Model model, HttpServletRequest req);
}
