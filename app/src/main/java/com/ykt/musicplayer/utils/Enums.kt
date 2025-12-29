package com.ykt.musicplayer.utils

enum class PlayerState {
    Idle,       // nothing loaded
    Buffering,  // loading media
    Playing,    // actively playing
    Paused,     // paused by user
    Ended,      // playback finished
    Error       // failed
}