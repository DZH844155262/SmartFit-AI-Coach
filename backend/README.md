## AI Coach Agent Architecture

User Training Data
|
v
Training Context Builder
|
+---- Nutrition Service
|
+---- Evidence Agent(PubMed)
|
v
Prompt Construction
|
v
DeepSeek LLM
|
v
Structured JSON Output
|
v
Validation
|
v
AI Training Analysis Storage
|
v
Future Recommendation

## Key Features


### 1. Context-aware AI Coach

The system does not directly ask LLM to generate recommendations.

It first aggregates:

- workout records
- training trends
- nutrition status
- scientific evidence


Then builds a structured context for LLM reasoning.


### 2. Evidence Grounded Recommendation

Training suggestions are generated with:

- PubMed evidence
- evidence limitations
- conservative progression strategy


### 3. AI Output Validation

LLM output is constrained by:

- JSON schema
- action enum validation
- historical data compatibility