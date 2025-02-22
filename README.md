# The Eco

**Eco** is an innovative mobile application that transforms captured images into music and lyrics. Built with Kotlin and Jetpack Compose, this app leverages modern Android development practices to provide a seamless user experience. Capture high-quality images, browse them in an inbuilt gallery, and generate unique lyrics and music inspired by your photos using Google's Gemini 2.0 Flash Experiment Model via the Google AI Studio API.

This project is actively under development, and we welcome contributions, feedback, and ideas to enhance its functionality!

## Features

### Done
- [x] **High-Quality Image Capture**: Use the back camera to take stunning photos directly within the app.
- [x] **Inbuilt Gallery**: View all captured photos in a single, easy-to-navigate gallery screen.
- [x] **Lyrics Generator**: Select an image from the gallery and generate creative lyrics inspired by it, powered by the Google Gemini 2.0 Flash Experiment Model (via Google AI Studio Test API).

### Planned
- [ ] **Music Generation**: Convert image-based lyrics into instrumental music using AI-driven audio synthesis.
- [ ] **Customizable Styles**: Allow users to choose music genres or lyric styles (e.g., poetic, rap, ballad).
- [ ] **Share Functionality**: Export generated lyrics and music to social media or save them locally.
- [ ] **Image Filters**: Apply filters to captured images to influence the mood of the generated lyrics/music.
- [ ] **Delete Captured Photos** : Delete multi selected images from the Gallery.

## Screenshots

- Image Capture Screen: `[Insert screenshot]`
- Gallery Screen: `[Insert screenshot]`
- Lyrics Generator Output: `[Insert screenshot]`

## Installation

To run the app locally, follow these steps:

### Prerequisites
- Android Studio (Latest stable version recommended)
- Kotlin 2.0 or higher
- A Google AI Studio API key for the Gemini 2.0 Flash Experiment Model

### Steps
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/MusicFromImages.git
   ```
2. **Open in Android Studio**:
   - Open Android Studio and select "Open an existing project."
   - Navigate to the cloned repository folder and click "Open."
3. **Configure API Key**:
   - Obtain an API key from [Google AI Studio](https://aistudio.google.com/).
   - Add the key to `local.properties` in the root directory:
     ```
     apiKey=your_api_key_here
     ```
4. **Build & Run**:
   - Sync the project with Gradle.
   - Connect an Android device or use an emulator.
   - Click "Run" in Android Studio.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Camera**: Android CameraX API
- **AI Model**: Google Gemini 2.0 Flash Experiment Model (via Google AI Studio API)
- **Dependency Management**: Gradle

## Contributing

We’d love for you to contribute to MusicFromImages! Here’s how:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m "Add your feature"`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a Pull Request.

Please ensure your code follows Kotlin conventions and includes appropriate comments.

## Contributors

- [BuddhikaChamodi](https://github.com/BuddhikaChamodi)
- [dulanjirashmika](https://github.com/dulanjirashmika)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Thanks to Google AI Studio for providing the Gemini 2.0 Flash Experiment Model and API.
- Inspired by the intersection of creativity, AI, and mobile technology.

## Contact

For questions, suggestions, or collaboration, reach out to [lahirucw1@gmail.com](mailto:lahirucw1@gmail.com) or open an issue on this repository.
