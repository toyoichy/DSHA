package com.deepseekharness.app.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class PluginInstallLinkTest {
  @Test
  public void managementEntryCannotCarryCommandsOrForeignOrigins() {
    assertTrue(PluginInstallLink.management("https://dsha.cc/app/plugins"));
    assertTrue(PluginInstallLink.management("https://dsha.cc/app/plugins/"));
    for (String value :
        new String[] {
          null,
          "http://dsha.cc/app/plugins",
          "https://dsha.cc.evil/app/plugins",
          "https://dsha.cc/app/plugins?command=x",
          "https://dsha.cc/app/plugins#x",
          "https://dsha.cc/app/../plugins"
        }) assertFalse(PluginInstallLink.management(value));
  }

  @Test
  public void acceptsBothEntryPointsAndDecodesExactlyOnce() {
    String query =
        "url=https%3A%2F%2Fgithub.com%2FMinglink%2Fdsh-infinite-gen-3%2Farchive%2Frefs%2Fheads%2Fmaster.zip&name=dsh-infinite-gen-3&version=0.5.0";
    assertEquals(
        "dsh-infinite-gen-3", PluginInstallLink.parse("https://dsha.cc/install/?" + query).name);
    assertTrue(PluginInstallLink.parse("dsha://install?" + query).url.endsWith("master.zip"));
    assertEquals(
        "dsh-web-mobile", PluginInstallLink.parse("dsha://install?builtin=dsh-web-mobile").builtin);
    assertEquals(
        "@scope/demo@1.2.3",
        PluginInstallLink.parse("dsha://install?url=%40scope%2Fdemo%401.2.3").url);
  }

  @Test
  public void ourFreeModelReleaseLinkIsPinnedToUpstreamDigest() {
    String url =
        "https://github.com/Ebony-Vinyl/dsh-our-free-model/releases/download/v2.8.5/"
            + "dsh-our-free-model-2.8.5.tgz";
    String digest = "680d514addabaa4774b4d8c6114063f9b2bee65244637ab695383a3887e50232";
    PluginInstallLink link =
        PluginInstallLink.parse(
            "dsha://install?url="
                + java.net.URLEncoder.encode(url, java.nio.charset.StandardCharsets.UTF_8)
                + "&sha256="
                + digest
                + "&name=dsh-our-free-model&version=2.8.5");
    assertEquals(url, link.url);
    assertEquals(digest, link.sha256);
    assertEquals("dsh-our-free-model", link.name);
    assertEquals("2.8.5", link.version);
  }

  @Test
  public void rejectsForeignHostsDuplicateFieldsAndCommands() {
    String[] invalid = {
      "https://evil.example/install/?url=https://example.com/a.zip",
      "dsha://install?url=file:///root/private.zip",
      "dsha://install?url=https://example.com/a.zip&url=https://example.com/b.zip",
      "dsha://install?url=https://example.com/a.zip&sha256=123",
      "dsha://install?command=rm%20-rf%20/",
      "https://dsha.cc/install/?builtin=../../data",
      "dsha://install?url=https://example.com/a.zip&name=a%0Ab"
    };
    for (String value : invalid) {
      try {
        PluginInstallLink.parse(value);
        fail(value);
      } catch (IllegalArgumentException expected) {
      }
    }
  }
}
