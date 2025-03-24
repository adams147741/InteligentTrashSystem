from fastapi import FastAPI, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy.orm import Session
import sqlalchemy
from sqlalchemy import Column, Integer, String, Float, true
from sqlalchemy.orm import sessionmaker, declarative_base

#pipinstall sqlalchemy and pymysql


app = FastAPI()

DATABASE_URL = "mysql+pymysql://system:system123@127.0.0.1:3306/odpadovysystem"

engine = sqlalchemy.create_engine(DATABASE_URL)
sessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

Base.metadata.create_all(bind=engine)

class UpdateDeviceRequest(BaseModel):
    weight_current: int
    volume_current: int
    aqi_inside: int
    aqi_outside: int

class InitialDeviceSetting(BaseModel):
    weight_max: int
    volume_max: int
    outpost: int

class NewDevice(BaseModel):
    weight_max: int
    volume_max: int
    description: str



class Device(Base):
    __tablename__ = "devices"
    id = Column(Integer, primary_key=True, index=true)
    outpost = Column(Integer,index=True)
    description = Column(String)
    weight_max = Column(Integer)
    weight_current = Column(Integer)
    volume_max = Column(Integer)
    volume_current = Column(Integer)
    aqi_inside = Column(Integer)
    aqi_outside = Column(Integer)

class Outpost(Base):
    __tablename__ = "outposts"
    id = Column(Integer, primary_key=True, index=True)
    description = Column(String)
    x_position = Column(Integer)
    y_position = Column(Integer)
    floor = Column(Integer)
    aqi_threshold = Column(Integer)

def get_db():
    db = sessionLocal()
    try:
        yield db
    finally:
        db.close()


@app.get("/devices")
def read_devices(db: Session = Depends(get_db)):
    return db.query(Device).all()

@app.get("/outposts")
def read_outpost(db: Session = Depends(get_db)):
    return db.query(Outpost).all()

@app.get("/devices/{id}")
def read_device(id: int, db: Session = Depends(get_db)):
    return db.query(Device).get(id)

@app.get("/outposts/{id}")
def read_outpost(id: int, db: Session = Depends(get_db)):
    return db.query(Outpost).get(id)

@app.get("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}


@app.put("/devices/{id}")
def update_device(id:int, request: UpdateDeviceRequest, db: Session = Depends(get_db)):
    device = db.query(Device).get(id)
    if not device:
        raise HTTPException(status_code=404, detail="Device not found")
    device.volume_current = request.volume_current
    device.weight_current = request.weight_current
    device.aqi_inside = request.aqi_inside
    device.aqi_outside = request.aqi_outside
    db.commit()
    db.refresh(device)
    return device

@app.put("/devices/initial/{id}")
def setup_device(id:int, request: InitialDeviceSetting, db: Session = Depends(get_db)):
    device = db.query(Device).get(id)
    if not device:
        raise HTTPException(status_code=404, detail="Device not found")
    device.weight_max = request.weight_max
    device.volume_max = request.volume_max
    device.outpost = request.outpost
    db.commit()
    db.refresh(device)
    return device


@app.post("/devices/new")
def create_device(request: NewDevice, db: Session = Depends(get_db)):
    device = Device(description=request.description, weight_max=request.weight_max, volume_max=request.volume_max,
                    volume_current=0, weight_current=0, aqi_inside=0, aqi_outside=0)
    db.add(device)
    db.commit()
    db.refresh(device)
    device = db.query(Device).get(device.id)
    return device



@app.delete("/devices/{id}")
def delete_device(id: int, db: Session = Depends(get_db)):
    device = db.query(Device).get(id)
    if not device:
        raise HTTPException(status_code=404, detail="Device not found")
    db.delete(device)
    db.commit()
    return {"ok": True}