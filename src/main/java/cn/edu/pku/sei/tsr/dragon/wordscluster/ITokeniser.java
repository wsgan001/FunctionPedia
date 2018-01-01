package cn.edu.pku.sei.tsr.dragon.wordscluster;

import java.util.List;



 /// <summary>
    /// �ִ����ӿ�
    /// </summary>
    public interface ITokeniser
    {
        List<String> partition(String input);
    }
