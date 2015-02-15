# gaejava_handson

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