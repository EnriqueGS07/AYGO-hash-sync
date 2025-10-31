async function enviarPeticion() {
  const bodyData = document.getElementById("inputData").value.trim();
  const submitBtn = document.getElementById("submitBtn");
  const responseContainer = document.getElementById("responseContainer");
  const responseContent = document.getElementById("responseContent");

  if (!bodyData) {
    responseContainer.style.display = "block";
    responseContent.textContent = "Error: Completa el campo";
    return;
  }

  submitBtn.disabled = true;
  submitBtn.textContent = "Enviando...";

  try {
    const response = await fetch(
      "http://ec2-54-159-20-80.compute-1.amazonaws.com:34003/registry",
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ key: bodyData }),
      }
    );

    const contentType = response.headers.get("content-type") || "";
    const payload = contentType.includes("application/json")
      ? await response.json()
      : await response.text();

    responseContainer.style.display = "block";
    if (response.ok) {
      responseContent.textContent = `Estado: ${response.status}\n${
        typeof payload === "string" ? payload : JSON.stringify(payload, null, 2)
      }`;
    } else {
      responseContent.textContent = `Error HTTP: ${response.status}\n${
        typeof payload === "string" ? payload : JSON.stringify(payload, null, 2)
      }`;
    }
  } catch (error) {
    responseContainer.style.display = "block";
    responseContent.textContent = `Error: ${error.message}`;
  } finally {
    submitBtn.disabled = false;
    submitBtn.textContent = "Enviar Petición";
  }
}

document.getElementById("submitBtn").addEventListener("click", enviarPeticion);

document.getElementById("inputData").addEventListener("keypress", (e) => {
  if (e.key === "Enter") enviarPeticion();
});
