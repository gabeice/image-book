# snail-fruit

An image uploading app with a [re-frame](https://github.com/Day8/re-frame) frontend and a datomic backend.

### Run Application:

Start datomic:
```
cd backend
$DATOMIC_HOME/bin/run -m datomic.peer-server -h localhost -p 8998 -a myaccesskey,mysecret -d image-book,datomic:mem://image-book
```

Start backend server:
```
make run
```

Start figwheel:

```
cd ../frontend
make clean
make figwheel
```
