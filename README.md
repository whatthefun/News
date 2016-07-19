News app
----------------
想抓幾個自己想要的新聞網站，以便自己閱讀新聞。
除了練習爬蟲還要學習側邊攔還有手勢模擬<br/>
附上google play網址: [News app](https://play.google.com/store/apps/details?id=yuan.test.user.news)


遇到的問題:


1. 爬蟲_Jsoup套件
[教學1](http://bioankeyang.blogspot.tw/2015/04/javajsouphtml-parser.htm)
[教學2](http://goo.gl/IsfPCb)
</br>注意事項:
    * 要在Manifest裡加入連線要求，如果是後來補加要求，可能要刪除app重新安裝
    * 要下載jsoup套件讓在libs裡面，並在gradle加入要編譯此檔案

2. ListView
[教學1](https://sites.google.com/site/givemepassxd999/android/yongbaseadapter-zi-dinglistview)
[教學2](http://huli.logdown.com/posts/280137-android-custom-listview)
</br>[進階](http://blog.coliam.net/?p=993):加入快速捲軸功能

3. TextView超連結
[教學](https://magiclen.org/android-html-textview/)

4. 側邊欄
</br>直接開新的NavigationView專案，自己從頭做比較麻煩

5. progressBar與側邊欄的衝突
</br>在NavigationViewActivity裡用progressBar時，會有錯誤
[教學](http://stackoverflow.com/questions/4250149/requestfeature-must-be-called-before-adding-content)

6. SwipeRefresh  向下拉可以更新的功能
[教學](https://www.youtube.com/watch?v=lAOr4BAnwsI)
</br>[進階](http://nlopez.io/swiperefreshlayout-with-listview-done-right/):解決結合ListView不能向下拉看其他資料的問題

7. 發現Google圖片的Html有兩種格式，來源又有兩種格式，所以要分類分好

