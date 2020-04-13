// UnityCallBack.aidl
package com.cloudminds.VirtualRobot;

// Declare any non-default types here with import statements

interface UnityCallBack {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

      void playFinish(String fileName);
      void playErro(String Filename);

    // 查询角色返回json
      void ResponseAvatarList(String json);

      //切换角色结果
      void ChangeAvatarResult(String name);

      void UnityAvatarsLoaded(String json);

      void playStart(String fileName);
      //测试
      //void unityTest01(String msgStrIn);

      //显示cloudia说话的文本
      void showVoiceTxt(String txtIn);
}
