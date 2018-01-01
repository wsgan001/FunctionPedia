package cn.edu.pku.sei.tsr.dragon.wordscluster;

import java.util.ArrayList;
import java.util.List;

     public class WawaCluster
    {
        public WawaCluster(int dataindex,double[] data)
        {
            CurrentMembership.add(dataindex);
            Mean = data;
        }

        /// <summary>
        /// �þ������ݳ�Ա����
        /// </summary>
         List<Integer> CurrentMembership = new ArrayList<Integer>();
        /// <summary>
        /// �þ��������
        /// </summary>
         double[] Mean;
        /// <summary>
        /// �÷�������������ľ�ֵ 
        /// </summary>
        /// <param name="coordinates"></param>
        public void UpdateMean(double[][] coordinates)
        {
            // ��� mCurrentMembership ȡ��ԭʼ���ϵ���� coord ���ö����� coordinates ��һ���Ӽ���
            //Ȼ��ȡ�����Ӽ��ľ�ֵ��ȡ��ֵ���㷨�ܼ򵥣����԰� coordinates �����һ�� m*n �ľ��� ,
            //ÿ����ֵ����ÿ�������е�ȡ��ƽ��ֵ , //��ֵ������ mCenter ��

            for (int i = 0; i < CurrentMembership.size(); i++)
            {
                double[] coord = coordinates[CurrentMembership.get(i)];
                for (int j = 0; j < coord.length; j++)
                {
                    Mean[j] += coord[j]; // �õ�ÿ�������еĺͣ�
                }
                for (int k = 0; k < Mean.length; k++)
                {
                    Mean[k] /= coord.length; // ��ÿ��������ȡƽ��ֵ
                }
            }
        }
    }

