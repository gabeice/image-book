# snail-fruit

An image uploading app with a [re-frame](https://github.com/Day8/re-frame) frontend and a datomic backend.

### Run Application:

Start datomic:
```
cd backend
./start_server.sh
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
