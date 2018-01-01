package cn.edu.pku.sei.tsr.dragon.wordscluster;

import java.util.Random;


    public class WawaKMeans
    {
        /// <summary>
        /// ���ݵ�����
        /// </summary>
        final int _coordCount;
        /// <summary>
        /// ԭʼ����
        /// </summary>
        final double[][] _coordinates;
        /// <summary>
        /// ���������
        /// </summary>
        final int _k;
        /// <summary>
        /// ����
        /// </summary>
        private  WawaCluster[] _clusters;

         WawaCluster[] getClusters()
        {
            return _clusters; 
            
        } 

        /// <summary>
        /// ����һ���������ڼ�¼�͸���ÿ�����ϵ������ĸ�Ⱥ����
        /// _clusterAssignments[j]=i;// ��ʾ�� j �����ϵ�������ڵ� i ��Ⱥ����
        /// </summary>
         final int[] _clusterAssignments;
        /// <summary>
        /// ����һ���������ڼ�¼�͸���ÿ�����ϵ���������
        /// </summary>
        private final int[] _nearestCluster;
        /// <summary>
        /// ����һ������������ʾ���ϵ㵽���ĵ�ľ���,
        /// ���С�_distanceCache[i][j]��ʾ��i�����ϵ㵽��j��Ⱥ�۶������ĵ�ľ��룻
        /// </summary>
        private final double[][] _distanceCache;
        /// <summary>
        /// ������ʼ�����������
        /// </summary>
        private static final Random _rnd = new Random(1);

        public WawaKMeans(double[][] data, int K)
        {
            _coordinates = data;
            _coordCount = data.length;
            _k = K;
            _clusters = new WawaCluster[K];
            _clusterAssignments = new int[_coordCount];
            _nearestCluster = new int[_coordCount];
            _distanceCache = new double[_coordCount][data.length];
            InitRandom();
        }

        public void Start()
        {
            int iter = 0;
            while (true)
            {
                System.out.println("Iteration " + (iter++) + "...");
                System.out.println(_clusters.length);
                //1�����¼���ÿ������ľ�ֵ
                for (int i = 0; i < _k; i++)
                {
                    _clusters[i].UpdateMean(_coordinates);
                }

                //2������ÿ�����ݺ�ÿ���������ĵľ���
                for (int i = 0; i < _coordCount; i++)
                {
                    for (int j = 0; j < _k; j++)
                    {
                        double dist = getDistance(_coordinates[i], _clusters[j].Mean);
                        _distanceCache[i][j] = dist;
                    }
                }

                //3������ÿ���������ĸ��������
                for (int i = 0; i < _coordCount; i++)
                {
                    _nearestCluster[i] = nearestCluster(i);
                }

                //4���Ƚ�ÿ����������ľ����Ƿ�����������ľ���
                //���ȫ��ȱ�ʾ���еĵ��Ѿ�����Ѿ����ˣ�ֱ�ӷ��أ�
                int k = 0;
                for (int i = 0; i < _coordCount; i++)
                {
                    if (_nearestCluster[i] == _clusterAssignments[i])
                        k++;

                }
                if (k == _coordCount)
                    break;

                //5��������Ҫ���µ������ϵ��Ⱥ����Ĺ�ϵ��������Ϻ������¿�ʼѭ����
                //��Ҫ�޸�ÿ������ĳ�Ա�ͱ�ʾĳ�����������ĸ�����ı���
                for (int j = 0; j < _k; j++)
                {
                    _clusters[j].CurrentMembership.clear();
                }
                for (int i = 0; i < _coordCount; i++)
                {
                    _clusters[_nearestCluster[i]].CurrentMembership.add(i);
                    _clusterAssignments[i] = _nearestCluster[i];
                }
                
            }

        }

        /// <summary>
        /// ����ĳ���������ĸ��������
        /// </summary>
        /// <param name="ndx"></param>
        /// <returns></returns>
        int nearestCluster(int ndx)
        {
            int nearest = -1;
            double min = Double.MAX_VALUE;
            for (int c = 0; c < _k; c++)
            {
                double d = _distanceCache[ndx][c];
                if (d < min)
                {
                    min = d;
                    nearest = c;
                }
          
            }
            if(nearest==-1)
            {
                ;
            }
            return nearest;
        }
        /// <summary>
        /// ����ĳ������ĳ�������ĵľ���
        /// </summary>
        /// <param name="coord"></param>
        /// <param name="center"></param>
        /// <returns></returns>
        static double getDistance(double[] coord, double[] center)
        {
//            int len = coord.length;
//            double sumSquared = 0.0;
//            for (int i = 0; i < len; i++)
//            {
//                double v = coord[i] - center[i];
//                sumSquared += v * v; //ƽ����
//            }
//            return Math.sqrt(sumSquared);

            //Ҳ���������Ҽн�������ĳ������ĳ�������ĵľ���
            return 1- TermVector.ComputeCosineSimilarity(coord, center);

        } 
        /// <summary>
        /// �����ʼ��k������
        /// </summary>
        private void InitRandom()
        {
            for (int i = 0; i < _k; i++)
            {
                int temp = _rnd.nextInt(_coordCount);
                _clusterAssignments[temp] = i; //��¼��temp���������ڵ�i������
                _clusters[i] = new WawaCluster(temp,_coordinates[temp]);
            }
        }
    }
