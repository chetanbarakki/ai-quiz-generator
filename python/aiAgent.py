from flask import Flask, request, jsonify
from dotenv import load_dotenv
from pydantic import BaseModel, Field
from langchain_groq import ChatGroq
from langchain_core.prompts import ChatPromptTemplate
from typing import List
import os

app = Flask(__name__)

# Load environment variables
load_dotenv()

# ---------------- MODELS ----------------

# FIX 1: Field names must match what your Java App expects
# Java looks for: "choices" (Array) and "correctIndex" (Int)
class Question(BaseModel):
    question: str = Field(description="The text of the question")
    choices: List[str] = Field(min_length=4, max_length=4, description="Exactly 4 options")
    correctIndex: int = Field(ge=0, le=3, description="Index of the correct option (0-3)")
    difficulty: str = Field(description="Difficulty level", default="HARD")

class ResearchResponse(BaseModel):
    topic: str
    difficulty: str
    questions: List[Question]

# ---------------- LLM & STRUCTURE ----------------

# FIX 2: Use a valid Groq model name
llm = ChatGroq(
    model="meta-llama/llama-4-scout-17b-16e-instruct",  # Or "llama3-8b-8192" for faster/cheaper results
    api_key=os.getenv("GROQ_API_KEY"),
    temperature=0 
)

structured_llm = llm.with_structured_output(ResearchResponse)

# ---------------- PROMPT ----------------

prompt = ChatPromptTemplate.from_messages([
    ("system", "You are an expert university examiner. Create a hard test based on the user's request."),
    ("human", "Topic: {topic}. Count: {count}. Generate the questions.")
])

chain = prompt | structured_llm

# ---------------- FUNCTION ----------------
@app.route('/generate-quiz', methods=['GET'])
def run_ai_agent():
    topic = request.args.get('topic', 'General Knowledge')
    count = request.args.get('count', 5)
    
    try:
        # Generate data
        response_obj = chain.invoke({"topic": topic, "count": count})
        
        # FIX 3: Serialize Pydantic object to Dictionary, then JSON
        # The Java app expects a List of questions, but our object is a wrapper (ResearchResponse).
        # We should return the list inside it to match Java's expected format, 
        # or return the whole object and update Java.
        # EASIEST FIX: Return just the list of questions to match your Java code.
        
        return jsonify(response_obj.dict()['questions'])
        
    except Exception as e:
        print(f"Error generating response: {e}")
        # Return a valid error JSON so Java doesn't just crash on 500
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    print(f"🚀 AI Quiz Agent running on port 5000...")
    app.run(port=5000)