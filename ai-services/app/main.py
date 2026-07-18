from fastapi import FastAPI

app = FastAPI(
    title="IntelliDocs AI Service",
    version="1.0.0"
)

@app.get("/")
def home():
    return {
        "message": "IntelliDocs AI Service Running"
    }