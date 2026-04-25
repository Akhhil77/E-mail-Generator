import { useState } from "react";
import axios from "axios";
import "./Home.css";
 
const toneOptions = ["", "Formal", "Casual", "Friendly", "Professional", "Apologetic", "Assertive"];

export default function Home(){

  const [emailContent, setEmailContent] = useState("");
  const [tone, setTone] = useState("");
  const [generatedReply, setGeneratedReply] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [copied, setCopied] = useState(false);
 
  const handleGenerate = async () => {
    if (!emailContent.trim()) return;
 
    setLoading(true);
    setError("");
    setGeneratedReply("");
 
    try {
      const response = await axios.post("http://localhost:8080/api/email/generate", {
        emailContent: emailContent,
        tone: tone,
      });
      setGeneratedReply(response.data);
    } catch (err) {
      setError("Failed to generate reply. Please check your connection and try again.");
    } finally {
      setLoading(false);
    }
  };
 
  const handleCopy = () => {
    navigator.clipboard.writeText(generatedReply).then(() => {
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    });
  };

   return (
  <div className="page">
    <div className="container">
      <h1 className="title">Email Reply Generator</h1>

      {/* Email Content Input */}
      <div className="fieldWrapper">
        <label className="label">Original Email Content</label>
        <textarea
          className="textarea"
          placeholder="Paste the email you want to reply to..."
          value={emailContent}
          onChange={(e) => setEmailContent(e.target.value)}
        />
      </div>

      {/* Tone Selector */}
      <div className="fieldWrapper">
        <label className="label">Tone (Optional)</label>
        <div className="selectWrapper">
          <select
            className="select"
            value={tone}
            onChange={(e) => setTone(e.target.value)}
          >
            {toneOptions.map((t) => (
              <option key={t} value={t}>
                {t === "" ? "-- Select a tone --" : t}
              </option>
            ))}
          </select>
          <span className="selectArrow">▾</span>
        </div>
      </div>

      {/* Generate Button */}
      <button
        className="button"
        onClick={handleGenerate}
        disabled={loading || !emailContent.trim()}
      >
        {loading ? "GENERATING..." : "GENERATE REPLY"}
      </button>

      {/* Error Message */}
      {error && <p className="error">{error}</p>}

      {/* Generated Reply */}
      {generatedReply && (
        <div className="resultSection">
          <p className="resultLabel">Generated Reply:</p>
          <div className="resultBox">
            <p className="resultText">{generatedReply}</p>
          </div>
          <button className="copyBtn" onClick={handleCopy}>
            {copied ? "Copied!" : "Copy reply"}
          </button>
        </div>
      )}
    </div>
  </div>
);
}