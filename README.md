# gaejava_handson

## Environment

* Eclipse
* Java JDK 1.7
* maven
* [DHC](https://chrome.google.com/webstore/detail/dhc-resthttp-api-client/aejoelaoggembcahagimdiliamlcdmfm)
* [Slim3](https://sites.google.com/site/slim3appengine/)

## SetUp

```
$ mvn install
$ mvn eclipse:eclipse -DdownloadSources=true
```

Eclipse -> Run As Web Application

以下にアクセスして、200が返ってくることを確認

```
http://localhost:8888
```

以下にアクセスして、Item Kindにデータが入っていることを確認

```
http://localhost:8888/_ah/admin/
```

## Hangs-on

### Part 0

環境整備

#### AppEngine Project 作成
https://appengine.google.com/ より、新しいApplicationを作成する

#### Set Config

master branchからStart
SetUp完了後に、 https://github.com/sinmetal/gaejava_handson/blob/master/src/main/webapp/WEB-INF/appengine-web.xml のapplicationIdを自分のApplicationのものに修正する

#### Deploy

```
$ {Your SDK PATH}/bin/appcfg.sh update src/main/webapp
```

https://appengine.google.com/ からDashboardに行き、Deployが行われたことを確認

#### Demo

@sinmetal がこれからどんなものを作るのかの説明をするよ。

### Part 1

新規登録APIを作成する

ClientからPostされたデータをDatastoreに新規登録するAPIを作成する
https://github.com/sinmetal/gaejava_handson/tree/handson/part1 に完成された品があるので、ちらちらとカンニングしながら、作っていく。

Slim3を利用した場合、APIを作る時は、以下をセットで作成します。

* Controller
* Model
* Service

#### Controller

ClientからのRequestを受け取る入り口
Slim3では命名規則に従って、Controller classを作ることで、Routingを行います。

今回はシンプルにいくつかの項目を登録する ItemControllerを作成します。
Restfulな形にしたいと思うので、同じPathで、HttpVerbごとに処理を分けます。
まずは新規登録のためにPOSTの処理を作ります。

https://github.com/sinmetal/gaejava_handson/blob/handson/part1/src/main/java/org/sinmetal/controller/ItemController.java

#### Model

DatastoreのKindと紐づくclass
Datastore low level APIは、Javaの型の利用が切ないため、Slim3がJavaBeanと紐付けを行います。

今回はClientから送られてくる内容と、作成したUserのemail、作成日、更新日を登録します。
ModelはJavaBeanの形式にいくつかのアノテーションを指定して作成します。

https://github.com/sinmetal/gaejava_handson/blob/handson/part1/src/main/java/org/sinmetal/model/Item.java

#### Service

Modelのユーティリティ class
Datastoreの操作はここでやります。

Modelを新規作成する関数と、ModelのKeyを生成するする関数を作ります。
Datastoreの場合、Keyには意味のある値を入れることが多いです。
ただ、今回のModelは特に情報を持っていてUniqueになる値が無いので、UUIDを指定します。
自動採番の機能もDatastoreにはありますが、Keyの生成にRPCが必要になるため、速度を優先した選択です。

https://github.com/sinmetal/gaejava_handson/blob/handson/part1/src/main/java/org/sinmetal/service/ItemService.java

#### Controller UnitTest

Codeを書くと同時にUnitTestも作っていきます。
Slim3はUnitTestについて豊富にサポートがあり、HTTPでRequestを送るところからテストを行えます。

https://github.com/sinmetal/gaejava_handson/blob/handson/part1/src/test/java/org/sinmetal/controller/ItemControllerTest.java

#### Service UnitTest

Service側にもUnitTestはあった方が楽です。
バグの箇所の切り分けや、HTTPが関わらないJavaの処理のみになるので、Parameterを作るのが簡単です。

https://github.com/sinmetal/gaejava_handson/blob/handson/part1/src/test/java/org/sinmetal/service/ItemServiceTest.java

#### Try

全て作り終えたら、Rest API Clientを利用して、確かめます。

/item にPOST Requetを送ってみます。
POSTの内容は以下のような形式です。

https://github.com/sinmetal/gaejava_handson/blob/handson/part1/src/test/resources/json/item/post_ok.json

POST Requestが成功したら、 http://localhost:8888/_ah/admin/ にアクセスして、Datastoreの内容を確認します。
AppEngineのDevServerにはDatastoreのMockがあるため、何も準備しなくても、local環境でDBを利用できます。

localで確認できたら、deployしてProductionでも確認します。
AppEngineにはAppのVersion管理の機能が入っているので、現行を残したまま新しいAppをdeployできます。

appengine-web.xmlのversionの値を2にしてdeployします。

https://github.com/sinmetal/gaejava_handson/blob/handson/part1/src/main/webapp/WEB-INF/appengine-web.xml

Versionを選択してAppを実行することもできます。
http://{version name}-dot-{your app id}.appspot.com で確認しましょう。

Version管理されているのはAppだけで、Datastore,Memcache,TaskQueueなど、RPCの向こう側は共通であることに注意。

### Part 2

一覧取得APIを作成する

ClientのGET Requestに対して、Datastoreから取得したModel一覧を返します。
https://github.com/sinmetal/gaejava_handson/tree/handson/part2 に完成された品があるので、ちらちらとカンニングしながら、作っていく。

ModelはPart1で作成しているので、ControllerとServiceに機能を追加していきます。

SimpleにQueryを実行するだけなら、以下のように書くだけで良いですが、今回のサンプルはもう少し用心深い実装にしています。
これはDatastoreのIndex遅延を考慮しているためです。

```
Datastore.query(meta).sort(meta.updatedAt.desc).asList();
```

https://github.com/sinmetal/gaejava_handson/blob/handson/part2/src/main/java/org/sinmetal/service/ItemService.java

Controllerは、Datastoreから取得した値をJsonにするだけなので、簡単です。

https://github.com/sinmetal/gaejava_handson/blob/handson/part2/src/main/java/org/sinmetal/controller/ItemController.java

#### Unit Test

Part1と同じように、ControllerとServiceにUnitTestを作成しましょう。
一覧取得できることを確認することになるので、事前にデータを登録する処理を入れます。

https://github.com/sinmetal/gaejava_handson/blob/handson/part2/src/test/java/org/sinmetal/controller/ItemControllerTest.java

https://github.com/sinmetal/gaejava_handson/blob/handson/part2/src/test/java/org/sinmetal/service/ItemServiceTest.java

#### Try

全て作り終えたら、Rest API Clientを利用して、確かめます。

/item にGET Requetを送ってみます。
Datastoreに登録されている内容が取得できれば、完成です。

localで確認ができたら、deployしてProduction環境でも確認してみましょう。

### Part 3

更新APIを作成する

ClientのPUT Requestに対して、Datastoreから該当のデータを取得し、更新を行います。
https://github.com/sinmetal/gaejava_handson/tree/handson/part3 に完成された品があるので、ちらちらとカンニングしながら、作っていく。

まず、更新APIを作る前に、IDを指定して対象の1件を取得するAPIを作成します。

Part2の時と同じようにControllerとServiceに処理を追加します。
URLを/item/{ID}にしたいので、 [Slim3 AppRouter](https://sites.google.com/site/slim3appengine/slim3-controller/url-mapping) を利用します。

https://github.com/sinmetal/gaejava_handson/blob/handson/part3/src/main/java/org/sinmetal/controller/AppRouter.java

このように設定しておけば、指定したURLの部分を、parameterとして置き換えて渡してくれます。

後はController側で、GET Requestの時にIDが無ければ一覧を返し、IDが指定されていれば、対象の1件を返すようにします。
今までと同じように、UnitTestを書いて、localで確認します。
deployしてProductionでの確認は、更新APIを作ってからで良いでしょう。

https://github.com/sinmetal/gaejava_handson/commit/848e1d1c268bc62543ef2525b4c83706bcb37caf


ここから欲しかった更新APIを作っていきます。

ControllerにPUT Requestが来た時の処理を追加します。

Serviceに更新処理を追加する時に、Transactionを利用しましょう。
Tx内で対象データをGetし、データを設定して、commitします。

```
Transaction tx = Datastore.beginTransaction();
```

https://github.com/sinmetal/gaejava_handson/commit/cf8c94dae7a05d4e9e94c2f57cb3f04223875a3e

### UnitTest

こちらもControllerとServiceにUnitTestを作成しましょう。

https://github.com/sinmetal/gaejava_handson/commit/cf8c94dae7a05d4e9e94c2f57cb3f04223875a3e

#### Try

全て作り終えたら、Rest API Clientを利用して、確かめます。

/item にPUT Requetを送ってみます。
Datastoreのデータが更新されたら成功です。

localで確認ができたら、deployしてProduction環境でも確認してみましょう。

### Part 3.1

更新APIに楽観的排他制御の機能を追加

WebAppでよく利用する楽観的排他制御の機能をSlim3がサポートしているので、使ってみましょう。
Modelにversion値を追加し、更新時に値が変わっていないか確認するようにします。

https://github.com/sinmetal/gaejava_handson/commit/7d3ca1869cacd565fab259009f9c97983f5fc2ea

### UnitTest

こちらもControllerとServiceにUnitTestを作成しましょう。

https://github.com/sinmetal/gaejava_handson/commit/7d3ca1869cacd565fab259009f9c97983f5fc2ea

### Part 4

削除APIを作成する

ClientからのDELETE Requestを受け取り、対象のデータを削除します。
https://github.com/sinmetal/gaejava_handson/tree/handson/part4 に完成された品があるので、ちらちらとカンニングしながら、作っていく。

ControllerとServiceにDelete用の機能を追加します。

https://github.com/sinmetal/gaejava_handson/commit/af127fe99bdff1c0a4184b3ff9882b41f37f96cd

### UnitTest

こちらもControllerとServiceにUnitTestを作成しましょう。

https://github.com/sinmetal/gaejava_handson/commit/cf8c94dae7a05d4e9e94c2f57cb3f04223875a3e

#### Try

全て作り終えたら、Rest API Clientを利用して、確かめます。

/itemに DELETE Requetを送ってみます。
Datastoreのデータが削除されたら成功です。

localで確認ができたら、deployしてProduction環境でも確認してみましょう。