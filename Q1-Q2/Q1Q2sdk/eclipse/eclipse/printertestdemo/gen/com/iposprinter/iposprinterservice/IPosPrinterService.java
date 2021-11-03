/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\android__work_program\\printertestdemo\\src\\com\\iposprinter\\iposprinterservice\\IPosPrinterService.aidl
 */
package com.iposprinter.iposprinterservice;
public interface IPosPrinterService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.iposprinter.iposprinterservice.IPosPrinterService
{
private static final java.lang.String DESCRIPTOR = "com.iposprinter.iposprinterservice.IPosPrinterService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.iposprinter.iposprinterservice.IPosPrinterService interface,
 * generating a proxy if needed.
 */
public static com.iposprinter.iposprinterservice.IPosPrinterService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.iposprinter.iposprinterservice.IPosPrinterService))) {
return ((com.iposprinter.iposprinterservice.IPosPrinterService)iin);
}
return new com.iposprinter.iposprinterservice.IPosPrinterService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getPrinterStatus:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPrinterStatus();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_printerInit:
{
data.enforceInterface(DESCRIPTOR);
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg0;
_arg0 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.printerInit(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setPrinterPrintDepth:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
_arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.setPrinterPrintDepth(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setPrinterPrintFontType:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
_arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.setPrinterPrintFontType(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setPrinterPrintFontSize:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
_arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.setPrinterPrintFontSize(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setPrinterPrintAlignment:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
_arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.setPrinterPrintAlignment(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_printerFeedLines:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
_arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.printerFeedLines(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_printBlankLines:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg2;
_arg2 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.printBlankLines(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_printText:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
_arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.printText(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_printSpecifiedTypeText:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg3;
_arg3 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.printSpecifiedTypeText(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_PrintSpecFormatText:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg4;
_arg4 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.PrintSpecFormatText(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_printColumnsText:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String[] _arg0;
_arg0 = data.createStringArray();
int[] _arg1;
_arg1 = data.createIntArray();
int[] _arg2;
_arg2 = data.createIntArray();
int _arg3;
_arg3 = data.readInt();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg4;
_arg4 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.printColumnsText(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_printBitmap:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
android.graphics.Bitmap _arg2;
if ((0!=data.readInt())) {
_arg2 = android.graphics.Bitmap.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg3;
_arg3 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.printBitmap(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_printBarCode:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
int _arg4;
_arg4 = data.readInt();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg5;
_arg5 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.printBarCode(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
return true;
}
case TRANSACTION_printQRCode:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg3;
_arg3 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.printQRCode(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_printRawData:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
_arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.printRawData(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_sendUserCMDData:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
_arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.sendUserCMDData(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_printerPerformPrint:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
_arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
this.printerPerformPrint(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.iposprinter.iposprinterservice.IPosPrinterService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
    * 打印机状态查询
    * @return 打印机当前状态
    * <ul>
    * <li>0:PRINTER_NORMAL 此时可以启动新的打印
    * <li>1:PRINTER_PAPERLESS  此时停止打印，如果当前打印未完成，加纸后需重打
    * <li>2:PRINTER_THP_HIGH_TEMPERATURE 此时暂停打印，如果当前打印未完成，冷却后将继续打印，无需重打
    * <li>3:PRINTER_MOTOR_HIGH_TEMPERATURE 此时不执行打印，冷却后需要初始化打印机，重新发起打印任务
    * <li>4:PRINTER_IS_BUSY    此时打印机正在打印
    * <li>5:PRINTE_ERROR_UNKNOWN  打印机异常
    */
@Override public int getPrinterStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPrinterStatus, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
    * 打印机初始化
    * 打印机上电并初始化默认设置
    * 使用时请查询打印机状态，PRINTER_IS_BUSY时请等待
    * @param callback 执行结果回调
    * @return
    */
@Override public void printerInit(com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printerInit, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
    * 设置打印机的打印浓度，对之后打印有影响，除非初始化
    * @param depth:     浓度等级,范围1-10,超出范围此功能不执行 默认等级6
    * @param callback 执行结果回调
    * @return
    */
@Override public void setPrinterPrintDepth(int depth, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(depth);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_setPrinterPrintDepth, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
    * 设置打印字体类型，对之后打印有影响，除非初始化
    * （目前只支持一种字体ST，后续会提供更多字体的支持）
    * @param typeface:     字体名称 ST(宋体)
    * @param callback  执行结果回调
    * @return
    */
@Override public void setPrinterPrintFontType(java.lang.String typeface, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(typeface);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_setPrinterPrintFontType, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	* 设置字体大小, 对之后打印有影响，除非初始化
	* 注意：字体大小是超出标准国际指令的打印方式，
	* 调整字体大小会影响字符宽度，每行字符数量也会随之改变，
	* 因此按等宽字体形成的排版可能会错乱，需自行调整
	* @param fontsize:	字体大小，目前支持的size有16、24、32、48，输入非法size执行默认值24
	* @param callback  执行结果回调
	* @return
	*/
@Override public void setPrinterPrintFontSize(int fontsize, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(fontsize);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_setPrinterPrintFontSize, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
    * 设置对齐方式，对之后打印有影响，除非初始化
    * @param alignment:	对齐方式 0--居左 , 1--居中, 2--居右,默认居中
    * @param callback  执行结果回调
    * @return
    */
@Override public void setPrinterPrintAlignment(int alignment, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(alignment);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_setPrinterPrintAlignment, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
    * 打印机走纸  (强制换行，结束之前的打印内容后走纸lines行，此时马达空转走纸，无数据传送给打印机)
    * @param lines：    打印机走纸行数(每行是一个像素点)
    * @param callback  执行结果回调
    * @return
    */
@Override public void printerFeedLines(int lines, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(lines);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printerFeedLines, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
    * 打印空白行  (强制换行，结束之前的打印内容后打印空白行，此时传送给打印机的数据全是0x00)
    * @param lines：    打印空白行数 限制最多100行
    * @param height：   空白行的高度(单位：像素点)
    * @param callback  执行结果回调
    * @return
    */
@Override public void printBlankLines(int lines, int height, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(lines);
_data.writeInt(height);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printBlankLines, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
    * 打印文字
    * 文字宽度满一行自动换行排版
    * @param text:	要打印的文字字符串
    * @param callback  执行结果回调
    * @return
    */
@Override public void printText(java.lang.String text, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(text);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printText, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
    * 打印指定字体类型和大小文本，字体设置只对本次有效
    * 文字宽度满一行自动换行排版
    * @param text:	要打印的文字字符串
    * @param typeface:  字体名称 ST（目前只支持一种）
    * @param fontsize:	字体大小，目前支持的size有16、24、32、48，输入非法size执行默认值24
    * @param callback  执行结果回调
    * @return
    */
@Override public void printSpecifiedTypeText(java.lang.String text, java.lang.String typeface, int fontsize, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(text);
_data.writeString(typeface);
_data.writeInt(fontsize);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printSpecifiedTypeText, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
    * 打印指定字体类型和大小文本，字体设置只对本次有效
    * 文字宽度满一行自动换行排版
    * @param text:	要打印的文字字符串
    * @param typeface:  字体名称 ST（目前只支持一种）
    * @param fontsize:	字体大小，目前支持的size有16、24、32、48，输入非法size执行默认值24
    * @param alignment:    对齐方式  (0居左, 1居中, 2居右)
    * @param callback  执行结果回调
    * @return
    */
@Override public void PrintSpecFormatText(java.lang.String text, java.lang.String typeface, int fontsize, int alignment, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(text);
_data.writeString(typeface);
_data.writeInt(fontsize);
_data.writeInt(alignment);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_PrintSpecFormatText, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	* 打印表格的一行，可以指定列宽、对齐方式
	* @param colsTextArr   各列文本字符串数组
	* @param colsWidthArr  各列宽度数组  总宽度不能大于((384 / fontsize) << 1)-（列数+1）
	*                      (以英文字符计算, 每个中文字符占两个英文字符, 每个宽度大于0),
	* @param colsAlign	        各列对齐方式(0居左, 1居中, 2居右)
	* @param isContinuousPrint   是否继续续打印表格 1：继续续打印  0：不继续打印
	* 备注: 三个参数的数组长度应该一致, 如果colsTextArr[i]的宽度大于colsWidthArr[i], 则文本换行
	* @param callback  执行结果回调
	* @return
	*/
@Override public void printColumnsText(java.lang.String[] colsTextArr, int[] colsWidthArr, int[] colsAlign, int isContinuousPrint, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStringArray(colsTextArr);
_data.writeIntArray(colsWidthArr);
_data.writeIntArray(colsAlign);
_data.writeInt(isContinuousPrint);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printColumnsText, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
    * 打印图片
    * @param alignment:	对齐方式 0--居左 , 1--居中, 2--居右, 默认居中
    * @param bitmapSize ： 位图大小，传入大小范围1~16,超出范围默认选择10 单位：24bit
    * @param mBitmap: 	图片bitmap对象(最大宽度384像素，超过无法打印并且回调callback异常函数)
    * @param callback  执行结果回调
    * @return
    */
@Override public void printBitmap(int alignment, int bitmapSize, android.graphics.Bitmap mBitmap, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(alignment);
_data.writeInt(bitmapSize);
if ((mBitmap!=null)) {
_data.writeInt(1);
mBitmap.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printBitmap, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	* 打印一维条码
	* @param data: 		条码数据
	* @param symbology: 	条码类型
	*    0 -- UPC-A，
	*    1 -- UPC-E，
	*    2 -- JAN13(EAN13)，
	*    3 -- JAN8(EAN8)，
	*    4 -- CODE39，
	*    5 -- ITF，
	*    6 -- CODABAR，
	*    7 -- CODE93，
	*    8 -- CODE128
	* @param height: 		条码高度, 取值1到16, 超出范围默认取6，每个单位代表24个像素点高度
	* @param width: 		条码宽度, 取值1至16, 超出范围默认取12，每个单位代表24个像素点长度
	* @param textposition:	文字位置 0--不打印文字, 1--文字在条码上方, 2--文字在条码下方, 3--条码上下方均打印
	* @param callback  执行结果回调
	* @return
	*/
@Override public void printBarCode(java.lang.String data, int symbology, int height, int width, int textposition, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(data);
_data.writeInt(symbology);
_data.writeInt(height);
_data.writeInt(width);
_data.writeInt(textposition);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printBarCode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	* 打印二维条码
	* @param data:			二维码数据
	* @param modulesize:	二维码块大小(单位:点, 取值 1 至 16 ),超出设置范围默认取值10
	* @param mErrorCorrectionLevel : 二维纠错等级(0:L 1:M 2:Q 3:H)
	* @param callback  执行结果回调
	* @return
	*/
@Override public void printQRCode(java.lang.String data, int modulesize, int mErrorCorrectionLevel, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(data);
_data.writeInt(modulesize);
_data.writeInt(mErrorCorrectionLevel);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printQRCode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	*打印原始的byte数据
	* @param rawPrintData    Byte 数据数据块
	* @param callback  结果回调
	*/
@Override public void printRawData(byte[] rawPrintData, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(rawPrintData);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printRawData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	* 使用ESC/POS指令打印
	* @param data	 指令
	* @param callback  结果回调
	*/
@Override public void sendUserCMDData(byte[] data, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(data);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_sendUserCMDData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	* 执行打印
	* 当执行完成各打印功能方法后，需要执行此方法，打印机才能执行打印；
	* 此方法执行之前需要判断打印机状态，当打印机处于PRINTER_NORMAL此方法有效，否则不执行。
	* @param feedlines: 打印并走纸freedlines点行
	* @param callback  结果回调
	*/
@Override public void printerPerformPrint(int feedlines, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(feedlines);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printerPerformPrint, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getPrinterStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_printerInit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setPrinterPrintDepth = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setPrinterPrintFontType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setPrinterPrintFontSize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_setPrinterPrintAlignment = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_printerFeedLines = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_printBlankLines = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_printText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_printSpecifiedTypeText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_PrintSpecFormatText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_printColumnsText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_printBitmap = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_printBarCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_printQRCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_printRawData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_sendUserCMDData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_printerPerformPrint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
}
/**
    * 打印机状态查询
    * @return 打印机当前状态
    * <ul>
    * <li>0:PRINTER_NORMAL 此时可以启动新的打印
    * <li>1:PRINTER_PAPERLESS  此时停止打印，如果当前打印未完成，加纸后需重打
    * <li>2:PRINTER_THP_HIGH_TEMPERATURE 此时暂停打印，如果当前打印未完成，冷却后将继续打印，无需重打
    * <li>3:PRINTER_MOTOR_HIGH_TEMPERATURE 此时不执行打印，冷却后需要初始化打印机，重新发起打印任务
    * <li>4:PRINTER_IS_BUSY    此时打印机正在打印
    * <li>5:PRINTE_ERROR_UNKNOWN  打印机异常
    */
public int getPrinterStatus() throws android.os.RemoteException;
/**
    * 打印机初始化
    * 打印机上电并初始化默认设置
    * 使用时请查询打印机状态，PRINTER_IS_BUSY时请等待
    * @param callback 执行结果回调
    * @return
    */
public void printerInit(com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
    * 设置打印机的打印浓度，对之后打印有影响，除非初始化
    * @param depth:     浓度等级,范围1-10,超出范围此功能不执行 默认等级6
    * @param callback 执行结果回调
    * @return
    */
public void setPrinterPrintDepth(int depth, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
    * 设置打印字体类型，对之后打印有影响，除非初始化
    * （目前只支持一种字体ST，后续会提供更多字体的支持）
    * @param typeface:     字体名称 ST(宋体)
    * @param callback  执行结果回调
    * @return
    */
public void setPrinterPrintFontType(java.lang.String typeface, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
	* 设置字体大小, 对之后打印有影响，除非初始化
	* 注意：字体大小是超出标准国际指令的打印方式，
	* 调整字体大小会影响字符宽度，每行字符数量也会随之改变，
	* 因此按等宽字体形成的排版可能会错乱，需自行调整
	* @param fontsize:	字体大小，目前支持的size有16、24、32、48，输入非法size执行默认值24
	* @param callback  执行结果回调
	* @return
	*/
public void setPrinterPrintFontSize(int fontsize, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
    * 设置对齐方式，对之后打印有影响，除非初始化
    * @param alignment:	对齐方式 0--居左 , 1--居中, 2--居右,默认居中
    * @param callback  执行结果回调
    * @return
    */
public void setPrinterPrintAlignment(int alignment, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
    * 打印机走纸  (强制换行，结束之前的打印内容后走纸lines行，此时马达空转走纸，无数据传送给打印机)
    * @param lines：    打印机走纸行数(每行是一个像素点)
    * @param callback  执行结果回调
    * @return
    */
public void printerFeedLines(int lines, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
    * 打印空白行  (强制换行，结束之前的打印内容后打印空白行，此时传送给打印机的数据全是0x00)
    * @param lines：    打印空白行数 限制最多100行
    * @param height：   空白行的高度(单位：像素点)
    * @param callback  执行结果回调
    * @return
    */
public void printBlankLines(int lines, int height, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
    * 打印文字
    * 文字宽度满一行自动换行排版
    * @param text:	要打印的文字字符串
    * @param callback  执行结果回调
    * @return
    */
public void printText(java.lang.String text, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
    * 打印指定字体类型和大小文本，字体设置只对本次有效
    * 文字宽度满一行自动换行排版
    * @param text:	要打印的文字字符串
    * @param typeface:  字体名称 ST（目前只支持一种）
    * @param fontsize:	字体大小，目前支持的size有16、24、32、48，输入非法size执行默认值24
    * @param callback  执行结果回调
    * @return
    */
public void printSpecifiedTypeText(java.lang.String text, java.lang.String typeface, int fontsize, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
    * 打印指定字体类型和大小文本，字体设置只对本次有效
    * 文字宽度满一行自动换行排版
    * @param text:	要打印的文字字符串
    * @param typeface:  字体名称 ST（目前只支持一种）
    * @param fontsize:	字体大小，目前支持的size有16、24、32、48，输入非法size执行默认值24
    * @param alignment:    对齐方式  (0居左, 1居中, 2居右)
    * @param callback  执行结果回调
    * @return
    */
public void PrintSpecFormatText(java.lang.String text, java.lang.String typeface, int fontsize, int alignment, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
	* 打印表格的一行，可以指定列宽、对齐方式
	* @param colsTextArr   各列文本字符串数组
	* @param colsWidthArr  各列宽度数组  总宽度不能大于((384 / fontsize) << 1)-（列数+1）
	*                      (以英文字符计算, 每个中文字符占两个英文字符, 每个宽度大于0),
	* @param colsAlign	        各列对齐方式(0居左, 1居中, 2居右)
	* @param isContinuousPrint   是否继续续打印表格 1：继续续打印  0：不继续打印
	* 备注: 三个参数的数组长度应该一致, 如果colsTextArr[i]的宽度大于colsWidthArr[i], 则文本换行
	* @param callback  执行结果回调
	* @return
	*/
public void printColumnsText(java.lang.String[] colsTextArr, int[] colsWidthArr, int[] colsAlign, int isContinuousPrint, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
    * 打印图片
    * @param alignment:	对齐方式 0--居左 , 1--居中, 2--居右, 默认居中
    * @param bitmapSize ： 位图大小，传入大小范围1~16,超出范围默认选择10 单位：24bit
    * @param mBitmap: 	图片bitmap对象(最大宽度384像素，超过无法打印并且回调callback异常函数)
    * @param callback  执行结果回调
    * @return
    */
public void printBitmap(int alignment, int bitmapSize, android.graphics.Bitmap mBitmap, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
	* 打印一维条码
	* @param data: 		条码数据
	* @param symbology: 	条码类型
	*    0 -- UPC-A，
	*    1 -- UPC-E，
	*    2 -- JAN13(EAN13)，
	*    3 -- JAN8(EAN8)，
	*    4 -- CODE39，
	*    5 -- ITF，
	*    6 -- CODABAR，
	*    7 -- CODE93，
	*    8 -- CODE128
	* @param height: 		条码高度, 取值1到16, 超出范围默认取6，每个单位代表24个像素点高度
	* @param width: 		条码宽度, 取值1至16, 超出范围默认取12，每个单位代表24个像素点长度
	* @param textposition:	文字位置 0--不打印文字, 1--文字在条码上方, 2--文字在条码下方, 3--条码上下方均打印
	* @param callback  执行结果回调
	* @return
	*/
public void printBarCode(java.lang.String data, int symbology, int height, int width, int textposition, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
	* 打印二维条码
	* @param data:			二维码数据
	* @param modulesize:	二维码块大小(单位:点, 取值 1 至 16 ),超出设置范围默认取值10
	* @param mErrorCorrectionLevel : 二维纠错等级(0:L 1:M 2:Q 3:H)
	* @param callback  执行结果回调
	* @return
	*/
public void printQRCode(java.lang.String data, int modulesize, int mErrorCorrectionLevel, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
	*打印原始的byte数据
	* @param rawPrintData    Byte 数据数据块
	* @param callback  结果回调
	*/
public void printRawData(byte[] rawPrintData, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
	* 使用ESC/POS指令打印
	* @param data	 指令
	* @param callback  结果回调
	*/
public void sendUserCMDData(byte[] data, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
/**
	* 执行打印
	* 当执行完成各打印功能方法后，需要执行此方法，打印机才能执行打印；
	* 此方法执行之前需要判断打印机状态，当打印机处于PRINTER_NORMAL此方法有效，否则不执行。
	* @param feedlines: 打印并走纸freedlines点行
	* @param callback  结果回调
	*/
public void printerPerformPrint(int feedlines, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
}
