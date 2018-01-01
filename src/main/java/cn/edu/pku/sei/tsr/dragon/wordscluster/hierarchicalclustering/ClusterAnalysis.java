package cn.edu.pku.sei.tsr.dragon.wordscluster.hierarchicalclustering;
import java.util.ArrayList;
import java.util.List;

public class ClusterAnalysis {
   public List<Cluster> startAnalysis(List<DataPoint> dataPoints,int ClusterNum,double maxDistanceOfClusters ){
      List<Cluster> finalClusters=new ArrayList<Cluster>();
    
      List<Cluster> originalClusters=initialCluster(dataPoints);
      finalClusters=originalClusters;
      //while(finalClusters.size()>ClusterNum){
      while(finalClusters.size()>1){
          double min=Double.MAX_VALUE;
          int mergeIndexA=0;
          int mergeIndexB=0;
          for(int i=0;i<finalClusters.size();i++){
              for(int j=0;j<finalClusters.size();j++){
                  if(i!=j){
                      Cluster clusterA=finalClusters.get(i);
                      Cluster clusterB=finalClusters.get(j);
                      List<DataPoint> dataPointsA=clusterA.getDataPoints();
                      List<DataPoint> dataPointsB=clusterB.getDataPoints();
                      for(int m=0;m<dataPointsA.size();m++){
                          for(int n=0;n<dataPointsB.size();n++){
                              double tempDis=getDistance(dataPointsA.get(m),dataPointsB.get(n));
                              if(tempDis<min){
                                  min=tempDis;
                                  mergeIndexA=i;
                                  mergeIndexB=j;
                              }
                          }
                      }
                  }
              } //end for j
          }// end for i
          //合并cluster[mergeIndexA]和cluster[mergeIndexB]
          System.out.println(min);
          if(min>maxDistanceOfClusters)
          {
        	  break;
          }
          finalClusters=mergeCluster(finalClusters,mergeIndexA,mergeIndexB);
      }//end while
      return finalClusters;
   }
   private List<Cluster> mergeCluster(List<Cluster> clusters,int mergeIndexA,int mergeIndexB){
       if (mergeIndexA != mergeIndexB) {
           // 将cluster[mergeIndexB]中的DataPoint加入到 cluster[mergeIndexA]
           Cluster clusterA = clusters.get(mergeIndexA);
           Cluster clusterB = clusters.get(mergeIndexB);
           List<DataPoint> dpA = clusterA.getDataPoints();
           List<DataPoint> dpB = clusterB.getDataPoints();
           for (DataPoint dp : dpB) {
               DataPoint tempDp = new DataPoint();
               tempDp.setDataPointName(dp.getDataPointName());
               tempDp.setDimensioin(dp.getDimensioin());
               tempDp.setCluster(clusterA);
               dpA.add(tempDp);
           }
           clusterA.setDataPoints(dpA);
           // List<Cluster> clusters中移除cluster[mergeIndexB]
           clusters.remove(mergeIndexB);
       }
       return clusters;
  }
  // 初始化类簇
  private List<Cluster> initialCluster(List<DataPoint> dataPoints){
      List<Cluster> originalClusters=new ArrayList<Cluster>();
      for(int i=0;i<dataPoints.size();i++){
          DataPoint tempDataPoint=dataPoints.get(i);
          List<DataPoint> tempDataPoints=new ArrayList<DataPoint>();
          tempDataPoints.add(tempDataPoint);
          Cluster tempCluster=new Cluster();
          tempCluster.setClusterName("Cluster "+String.valueOf(i));
          tempCluster.setDataPoints(tempDataPoints);
          tempDataPoint.setCluster(tempCluster);
          originalClusters.add(tempCluster);
      }
      return originalClusters;
  }
  //计算两个样本点之间的欧几里得距离
  private double getDistance(DataPoint dpA,DataPoint dpB){
       double distance=0;
       double[] dimA = dpA.getDimensioin();
       double[] dimB = dpB.getDimensioin();
       if (dimA.length == dimB.length) {
           for (int i = 0; i < dimA.length; i++) {
                double temp=Math.pow((dimA[i]-dimB[i]),2);
                distance=distance+temp;
           }
           distance=Math.pow(distance, 0.5);
       }
      return distance;
  }
  public static void main(String[] args){
      ArrayList<DataPoint> dpoints = new ArrayList<DataPoint>();
    
      double[] a={2,3};
      double[] b={2,4};
      double[] c={1,4};
      double[] d={1,3};
      double[] e={2,2};
      double[] f={3,2};
      double[] g={8,7};
      double[] h={8,6};
      double[] i={7,7};
      double[] j={7,6};
      double[] k={8,5};
//      double[] l={100,2};//孤立点

      double[] m={8,20};
      double[] n={8,19};
      double[] o={7,18};
      double[] p={7,17};
      double[] q={8,20};
      dpoints.add(new DataPoint(a,"a"));
      dpoints.add(new DataPoint(b,"b"));
      dpoints.add(new DataPoint(c,"c"));
      dpoints.add(new DataPoint(d,"d"));
      dpoints.add(new DataPoint(e,"e"));
      dpoints.add(new DataPoint(f,"f"));
      dpoints.add(new DataPoint(g,"g"));
      dpoints.add(new DataPoint(h,"h"));
      dpoints.add(new DataPoint(i,"i"));
      dpoints.add(new DataPoint(j,"j"));
      dpoints.add(new DataPoint(k,"k"));
//      dataPoints.add(new DataPoint(l,"l"));
      dpoints.add(new DataPoint(m,"m"));
      dpoints.add(new DataPoint(n,"n"));
      dpoints.add(new DataPoint(o,"o"));
      dpoints.add(new DataPoint(p,"p"));
      dpoints.add(new DataPoint(q,"q"));
      int clusterNum=3; //类簇数
      ClusterAnalysis ca=new ClusterAnalysis();
      List<Cluster> clusters=ca.startAnalysis(dpoints, clusterNum,2);
      for(Cluster cl:clusters){
          System.out.println("------"+cl.getClusterName()+"------");
          List<DataPoint> tempDps=cl.getDataPoints();
          for(DataPoint tempdp:tempDps){
              System.out.println(tempdp.getDataPointName());
          }
      }
  }
}
