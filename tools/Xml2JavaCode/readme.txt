1、xml的配置文件生成Java代码，需要再每个配置文件的根节点下添加数据类型的定义：
如：<Root id="int" type="int" para="int" gold="int",arraytest="[int]",maptest="[int,string]">
       <Data id="1" type="1" para="1" gold="1000" arraytest="1|2|3|4",maptest="1;2|3;4|5;6" />
	   
	数据类型的定义要覆盖所有的数据。
	数据类型支持 
	（1）、基本类型
	int，long，float，double，string，boolean
	
	（2）、数组类型，数据之间以“|”分割，如：1|2|3|4
	[int]，[long]，[float]，[double]，[string]，[boolean]
	
	（3）、字典类型（map），数据以;和|分割
	(int,int)，(int,long)，(int,string)，(int,float)，(int,double)，(int,boolean)
	(string,int)，(string,long)，(string,string)，(string,float)，(string,double)，(string,boolean)
	
2、xml文件所在的路径和java代码生成的目录在conf.ini中配置
	（可以直接指定开发的工作目录，无需再拷贝了，每次生成覆盖）