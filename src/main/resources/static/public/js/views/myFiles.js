async function fetchFiles() {
    // test
    const {data} = await axios.get('/files/899adb4e-6a48-407a-805e-1d9ccbe7b46a');
    console.log(data);
}