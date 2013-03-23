本文档适用对象：
	本文档适用于之前使用过百度地图SDK的开发者，即使用过1.3.5及之前版本进行开发的人员。
------------------------------------------------------------------------------------------
一、使用地图之前，要先初始化地图管理模块。
	代码如下：
	BMapManager mBMapManager = new BMapManager(context);
	mBMapManager.init("此处填写你申请的key",new MyGeneralListener()) // MyGeneralListener继承自MKGeneralListener接口
	mBMapManager.start();

二、废弃了MapActivity类，使用MapView的Activity，自2.0以后无需再继承自MapActivity，可以在任意的Activity中使用。

三、MapView的onPause、onResume、destroy、onSaveInstanceState、onRestoreInstanceState方法，要在相应的Activity的onPause、onResume、onDestroy、onSaveInstanceState、onRestoreInstanceState方法中调用。
	示例代码：
	protected void onPause() {
        mMapView.onPause();
        super.onPause();
    } //其他代码类似，这里不一一举例。

四、MapView添加一个Overlay之后，调用MapView的refresh()方法(2.0以后)，替代invalidate()方法(1.3.5之前)。

PS：自2.0.0以后，有些类的包名可能改变，但只要类名不变，功能基本不变。用的时候需要注意。Overlay废弃了draw接口，自2.0以后，暂不支持自己绘制的Overlay。

更多详细信息，请参考官网的开发指南及Demo示例代码。