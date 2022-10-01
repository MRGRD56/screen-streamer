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
        }
    </style>
</head>
<body>
<img class="screen-stream" alt="Screen stream" src="/api/screen/stream.mjpeg?password=${password!""}">

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

    const handleClick = (event) => {
        event.preventDefault();

        const {x, y} = getPositionFromEvent(event);
        const apiButton = getMouseButtonFromEvent(event);

        console.log('x: ' + x + '; y: ' + y + '; button: ' + apiButton);

        requestClick({
            x,
            y,
            mouseButton: apiButton
        });
    };

    screenElement.addEventListener('contextmenu', event => {
        event.preventDefault();
        return false;
    });

    screenElement.addEventListener('click', handleClick);
    screenElement.addEventListener('auxclick', handleClick);

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
</script>
</body>
</html>