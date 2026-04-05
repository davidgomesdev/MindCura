# MindCura Chat Interface

A beautiful, therapeutic web interface for the MindCura psychotherapy AI agent.

## Features

✨ **Lavender-themed UI**: Calming color scheme designed for therapeutic conversations
- Soft lavender gradients and accent colors
- Responsive design that works on desktop and mobile
- Smooth animations and transitions

🎭 **Multiple Personas**: Choose your preferred therapeutic approach
- **Compassionate Listener**: Empathetic and validating responses
- **Cognitive Coach**: Focus on thought patterns and behaviors
- **Mindfulness Guide**: Meditation and present-moment awareness

💬 **Real-time Streaming**: Watch responses appear character by character
- NDJSON streaming from the backend API
- Seamless message updates
- Loading indicators while the agent thinks

📊 **Session Metrics**: Track conversation details
- Token usage statistics
- Response time tracking
- Trace ID for debugging and monitoring

🔒 **CORS Enabled**: Already configured for local development
- Localhost addresses pre-configured
- Easy to expand to production domains

## Usage

### Starting the Application

1. **Build and run the Quarkus application:**
   ```bash
   cd /Users/gomesd/Personal/mindcura
   ./gradlew quarkusDev
   ```

2. **Open the chat interface:**
   - Navigate to `http://localhost:8080/` in your web browser
   - The interface will load automatically

### Using the Chat

1. **Select a persona** from the sidebar (left panel on desktop)
   - Click any persona button to switch therapeutic approaches

2. **Type your message** in the input field at the bottom
   - Press `Enter` to send (or `Shift+Enter` for new line)
   - Click the "Send" button to submit

3. **Watch the response** stream in real-time
   - The agent's response appears token by token
   - Session stats appear when the response completes

4. **View session information** in the sidebar
   - Trace ID for monitoring
   - Token counts and response time
   - Status indicator

## Architecture

### Frontend Files
- **Location**: `src/main/resources/static/index.html`
- Single HTML file with embedded CSS and JavaScript
- No external dependencies required
- Responsive design using CSS Grid and Flexbox

### Backend Integration
- **Endpoint**: `PUT /chat`
- **Request Format**: JSON with `input` and `persona` fields
- **Response Format**: NDJSON (newline-delimited JSON)
- **Events**: `start`, `token`, `sources`, `done`

### Event Stream Format

The backend streams NDJSON events:

```json
{"type":"start","traceId":"abc123..."}
{"type":"token","value":"Hello"}
{"type":"token","value":" there"}
{"type":"done","tokensUsed":150,"timeTaken":"2.34s"}
```

## Configuration

### CORS Settings
Currently configured for localhost development. To add production domains, edit `src/main/resources/application.yaml`:

```yaml
quarkus:
  http:
    cors:
      enabled: true
      origins:
        - http://localhost:8080
        - http://127.0.0.1:8080
        - http://localhost:8081
        - http://127.0.0.1:8081
        # Add your production domain here
        - https://your-domain.com
```

### API Base URL
The chat interface connects to `http://localhost:8080` by default. To change this, modify the `API_BASE` constant in the JavaScript section of `index.html`.

## Customization

### Colors
The color scheme is defined using CSS variables at the top of the style block:

```css
:root {
    --primary-lavender: #b19cd9;
    --light-lavender: #e6d7f5;
    --dark-lavender: #7851a9;
    /* ... more colors ... */
}
```

Simply change these hex values to customize the theme.

### Messages
Initial greeting and persona descriptions can be edited directly in the HTML. Look for:
- Initial greeting in the `.chat-messages` div
- Persona buttons with their labels

## Browser Support

- Chrome/Edge: Full support
- Firefox: Full support
- Safari: Full support
- Mobile browsers: Responsive design included

## Troubleshooting

### Messages not sending
1. **Check backend is running**
   - Verify the server started with `./gradlew quarkusDev`
   - Visit `http://localhost:8080` - you should see the chat page load

2. **Check the backend logs**
   - Look for errors in the terminal running `quarkusDev`
   - Check if the `/chat` endpoint is being called

3. **Verify API configuration**
   - Ensure `model.anthropic.api-key` is set in `application.yaml` (not `REPLACE_ME`)
   - The Anthropic API key is required for the model to respond

4. **Check browser console**
   - Open Developer Tools (F12 or Cmd+Option+I)
   - Check the Console tab for JavaScript errors
   - Check the Network tab to see if the `/chat` request is being made
   - Look for CORS errors if the request is blocked

### The page won't load
1. **Check static file serving**
   - Visit `http://localhost:8080/index.html` directly
   - If this works but `/` doesn't, the root route may need configuration

2. **Check firewall/localhost access**
   - Verify `http://localhost:8080` is accessible (not blocked by firewall)
   - Try `http://127.0.0.1:8080` if localhost doesn't work

3. **Port already in use**
   - Check if port 8080 is already in use: `lsof -i :8080`
   - Kill the process or change the port in `application.yaml`

### No responses from AI
1. **API Key not configured**
   - Edit `src/main/resources/application.yaml`
   - Replace `REPLACE_ME` with your actual Anthropic API key
   - Restart the server with `./gradlew quarkusDev`

2. **Model not responding**
   - Check the backend logs for error messages
   - Verify the Claude model ID is valid (default: `claude-haiku-4-5`)
   - Check network connection to Anthropic API

3. **Timeout issues**
   - The default timeout is 60 seconds
   - If responses are slow, check the response time in session info
   - May need to increase timeout in `application.yaml` under `model.anthropic.timeout`

### Styling issues
1. Clear browser cache (Ctrl+Shift+Delete or Cmd+Shift+Delete)
2. Hard refresh the page (Ctrl+F5 or Cmd+Shift+R)
3. Check that CSS variables are supported in your browser
4. Ensure JavaScript is enabled

## Notes

- This is a single HTML file with embedded styles and JavaScript
- No build process required for the frontend
- Changes to `index.html` are reflected immediately when `quarkusDev` is running
- All conversations are stateless - no data is persisted
- For mental health emergencies, please contact appropriate mental health services

