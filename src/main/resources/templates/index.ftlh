<#import "/spring.ftl" as spring/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Screen Stream</title>

    <style>
        body {
            background: black;
        }

        .screen-stream {
            position: absolute;
            top: 0;
            bottom: 0;
            left: 0;
            right: 0;
            margin: auto;
            max-height: 100vh;
            max-width: 100%;
            touch-action: manipulation;

            user-drag: none;
            -webkit-user-drag: none;
            user-select: none;
            -moz-user-select: none;
            -webkit-user-select: none;
            -ms-user-select: none;
        }
    </style>
</head>
<body>
<img class="screen-stream" alt="Screen stream" src="/api/screen/stream.mjpeg?password=${password!""}">

<script src="https://cdn.jsdelivr.net/npm/lodash@4.17.21/lodash.min.js"></script>
<script>
    const PASSWORD = `${password!""}` || undefined;

    const request = (url, data) => {
        return fetch(url, {
            ...data,
            body: data.body != null ? JSON.stringify(data.body) : data.body,
            headers: {
                'Content-Type': 'application/json',
                ...data.headers,
                'X-Password': PASSWORD
            }
        });
    };

    const screenElement = document.querySelector('.screen-stream');

    const getPosition = ({x, y}) => {
        const width = screenElement.offsetWidth;
        const height = screenElement.offsetHeight;

        const screenX = x / width;
        const screenY = y / height;

        return {
            x: screenX,
            y: screenY
        };
    };

    const getPositionFromEvent = event => {
        const x = event.offsetX;
        const y = event.offsetY;

        // const screenWidth = screenElement.naturalWidth;
        // const screenHeight = screenElement.naturalHeight;

        return getPosition({x, y});
    };

    const getMouseButtonFromEvent = event => {
        const {button} = event;
        return {
            0: 'LEFT',
            1: 'MIDDLE',
            2: 'RIGHT'
        }[button];
    };

    const getKeyboardKeyFromEvent = event => {
        return {
            keycode: event.which
        };
    };

    /** @deprecated */
    const requestClick = ({x, y, mouseButton}) => {
        request('/api/screen-control/mouse/click', {
            method: 'POST',
            body: {
                x,
                y,
                mouseButton
            }
        });
    };

    const requestButton = (eventType, {x, y, mouseButton}) => {
        request('/api/screen-control/mouse/' + eventType, {
            method: 'POST',
            body: {
                x,
                y,
                mouseButton
            }
        });
    };

    const handleMouseButton = (event) => {
        event.preventDefault();

        const apiButton = getMouseButtonFromEvent(event);
        const {x, y} = getPositionFromEvent(event);
        const eventType = {
            'mousedown': 'press',
            'mouseup': 'release'
        }[event.type];

        console.log('x: ' + x + '; y: ' + y + '; button: ' + apiButton);

        requestButton(eventType, {
            x,
            y,
            mouseButton: apiButton
        });
    };

    screenElement.addEventListener('contextmenu', event => {
        event.preventDefault();
        return false;
    });

    // click, auxclick
    screenElement.addEventListener('mousedown', handleMouseButton);
    screenElement.addEventListener('mouseup', handleMouseButton);

    function onLongPress(element, callback) {
        let timer;

        element.addEventListener('touchstart', (event) => {
            if (event.targetTouches.length > 1) {
                return;
            }

            timer = setTimeout(() => {
                timer = null;
                callback(event);
            }, 500);
        });

        function cancel() {
            clearTimeout(timer);
        }

        element.addEventListener('touchend', cancel);
        element.addEventListener('touchmove', cancel);
    }

    onLongPress(screenElement, event => {
        event.preventDefault();

        const rect = event.target.getBoundingClientRect();

        const {x, y} = getPosition({
            x: event.targetTouches[0].pageX - rect.left,
            y: event.targetTouches[0].pageY - rect.top
        });

        console.log('x: ' + x + '; y: ' + y + '; button: RIGHT');

        requestClick({
            x,
            y,
            mouseButton: 'RIGHT'
        });
    });

    screenElement.addEventListener('wheel', event => {
        event.preventDefault();

        request('/api/screen-control/mouse/scroll', {
            method: 'POST',
            body: {
                deltaX: Math.sign(event.deltaX),
                deltaY: Math.sign(event.deltaY)
            }
        });
    });

    screenElement.addEventListener('mousemove', _.throttle(event => {
        event.preventDefault();

        const {x, y} = getPositionFromEvent(event);

        request('/api/screen-control/mouse/move', {
            method: 'POST',
            body: {
                x,
                y
            }
        });
    }, 50));

    const handleKeyboardKey = event => {
        event.preventDefault();

        const {keycode} = getKeyboardKeyFromEvent(event);
        const eventType = {
            'keydown': 'key-down',
            'keyup': 'key-up'
        }[event.type];

        request('/api/screen-control/keyboard/' + eventType, {
            method: 'POST',
            body: {
                keycode
            }
        });
    };

    document.addEventListener('keydown', handleKeyboardKey, true);
    document.addEventListener('keyup', handleKeyboardKey, true);
</script>
</body>
</html>